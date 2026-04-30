package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.quanxiaoha.weblog.admin.dao.*;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.admin.model.vo.article.*;
import com.quanxiaoha.weblog.admin.service.AdminArticleService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private AdminArticleDao articleDao;
    @Autowired
    private AdminArticleContentDao articleContentDao;
    @Autowired
    private AdminArticleCategoryRelDao articleCategoryRelDao;
    @Autowired
    private AdminTagDao tagDao;
    @Autowired
    private AdminArticleTagRelDao articleTagRelDao;
    @Autowired
    private AdminUserDao userDao;
    @Autowired
    private CategoryMapper categoryMapper;

    // 手动事务
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AdminArticleServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        UserDO currentUser = getCurrentUser();
        assertCategoryOwnedBy(publishArticleReqVO.getCategoryId(), currentUser.getId());
        boolean isExecuteSuccess = transactionTemplate.execute(status -> {
            ArticleDO articleDO = ArticleDO.builder()
                    .userId(currentUser.getId())
                    .title(publishArticleReqVO.getTitle())
                    .titleImage(publishArticleReqVO.getTitleImage())
                    .description(publishArticleReqVO.getDescription())
                    .visibility(normalizeVisibility(publishArticleReqVO.getVisibility()))
                    .build();
            articleDao.insertArticle(articleDO);

            Long articleId = articleDO.getId();

            ArticleContentDO articleContentDO = ArticleContentDO.builder()
                    .articleId(articleId)
                    .content(publishArticleReqVO.getContent())
                    .build();
            articleContentDao.insertArticleContent(articleContentDO);

            // 所属分类
            ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                    .articleId(articleId)
                    .categoryId(publishArticleReqVO.getCategoryId())
                    .build();
            articleCategoryRelDao.insert(articleCategoryRelDO);

            // 标签
            // 提交的标签
            List<String> publishTags = publishArticleReqVO.getTags();
            handleTagBiz(articleId, publishTags, currentUser.getId());
            return true;
        });

        return isExecuteSuccess ? Response.success() : Response.fail();
    }

    @Override
    public Response queryArticleDetail(QueryArticleDetailReqVO queryArticleDetailReqVO) {
        Long articleId = queryArticleDetailReqVO.getArticleId();
        ArticleDO articleDO = getOwnArticle(articleId);
        ArticleContentDO articleContentDO = articleContentDao.queryByArticleId(articleId);

        // 所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelDao.selectByArticleId(articleId);

        // 对应标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelDao.selectByArticleId(articleId);
        List<Long> tagIds = CollectionUtils.isEmpty(articleTagRelDOS)
                ? Collections.emptyList()
                : articleTagRelDOS.stream().map(p -> p.getTagId()).collect(Collectors.toList());

        // 历史或异常数据下 content / 分类关联可能不存在，需防空指针（否则 500 被包装成“后台小哥努力修复中”）
        String content = articleContentDO == null ? "" : articleContentDO.getContent();
        Long categoryId = articleCategoryRelDO == null ? null : articleCategoryRelDO.getCategoryId();

        QueryArticleDetailRspVO queryArticleDetailRspVO = QueryArticleDetailRspVO.builder()
                .id(articleDO.getId())
                .title(articleDO.getTitle())
                .titleImage(articleDO.getTitleImage())
                .content(content)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .description(articleDO.getDescription())
                .visibility(articleDO.getVisibility())
                .build();

        return Response.success(queryArticleDetailRspVO);
    }

    @Override
    public Response queryArticlePageList(QueryArticlePageListReqVO queryArticlePageListReqVO) {
        Long current = queryArticlePageListReqVO.getCurrent();
        Long size = queryArticlePageListReqVO.getSize();
        Date startDate = queryArticlePageListReqVO.getStartDate();
        Date endDate = queryArticlePageListReqVO.getEndDate();
        String searchTitle = queryArticlePageListReqVO.getSearchTitle();

        Page<ArticleDO> articleDOPage = articleDao.queryArticlePageList(current, size, startDate, endDate, searchTitle, getCurrentUser().getId());

        return Response.success(articleDOPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getArticleId();
        getOwnArticle(articleId);
        articleDao.deleteById(articleId);
        articleContentDao.deleteByArticleId(articleId);
        return Response.success();
    }

    @Override
    // @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        assertCategoryOwnedBy(updateArticleReqVO.getCategoryId(), getCurrentUser().getId());
        boolean isExecuteSuccess = transactionTemplate.execute(status -> {
            Long articleId = updateArticleReqVO.getId();
            getOwnArticle(articleId);
            UserDO currentUser = getCurrentUser();

            ArticleDO articleDO = ArticleDO.builder()
                    .id(articleId)
                    .title(updateArticleReqVO.getTitle())
                    .titleImage(updateArticleReqVO.getTitleImage())
                    .description(updateArticleReqVO.getDescription())
                    .visibility(normalizeVisibility(updateArticleReqVO.getVisibility()))
                    .updateTime(new Date())
                    .build();
            articleDao.updateById(articleDO);

            ArticleContentDO articleContentDO = ArticleContentDO.builder()
                    .articleId(articleId)
                    .content(updateArticleReqVO.getContent())
                    .build();
            // 仅 update 时若库中无 content 行会更新 0 行，需补 insert，避免详情永远无正文
            int contentRows = articleContentDao.updateByArticleId(articleContentDO);
            if (contentRows == 0) {
                articleContentDao.insertArticleContent(articleContentDO);
            }

            // 更新文章分类
            articleCategoryRelDao.deleteByArticleId(articleId);
            ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                    .articleId(articleId)
                    .categoryId(updateArticleReqVO.getCategoryId())
                    .build();
            articleCategoryRelDao.insert(articleCategoryRelDO);

            // 更新文章标签
            articleTagRelDao.deleteByArticleId(articleId);
            // 提交的标签
            List<String> publishTags = updateArticleReqVO.getTags();
            handleTagBiz(articleId, publishTags, currentUser.getId());
            return true;
        });

        return isExecuteSuccess ? Response.success() : Response.fail();
    }

    /**
     * 处理标签相关业务
     * @param articleId
     * @param publishTags
     */
    public void handleTagBiz(Long articleId, List<String> publishTags, Long userId) {
        if (CollectionUtils.isEmpty(publishTags)) {
            return;
        }
        List<TagDO> tagDOS = tagDao.selectAllByUserId(userId);

        // 筛选出库中不存在的标签
        List<String> noExistTags = null;
        // 库中已存在的标签
        List<String> existTags = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            List<String> tagIds = tagDOS.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.toList());
            noExistTags = publishTags.stream().filter(p -> !tagIds.contains(p)).collect(Collectors.toList());
            existTags = publishTags.stream().filter(p -> tagIds.contains(p)).collect(Collectors.toList());
        } else {
            noExistTags = publishTags;
        }

        // 不存在的标签先入库
        if (!CollectionUtils.isEmpty(noExistTags)) {
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            noExistTags.forEach(noExistTag -> {
                TagDO tagDO = TagDO.builder()
                        .userId(userId)
                        .name(noExistTag)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .isDeleted(false)
                        .build();

                tagDao.insert(tagDO);
                Long tagId = tagDO.getId();

                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });

            articleTagRelDao.insertBatch(articleTagRelDOS);
        }

        if (!CollectionUtils.isEmpty(existTags)) {
            List<ArticleTagRelDO> articleTagRelDOS = Lists.newArrayList();
            existTags.forEach(existTagId -> {
                ArticleTagRelDO articleTagRelDO = ArticleTagRelDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(existTagId))
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            articleTagRelDao.insertBatch(articleTagRelDOS);
        }
    }

    private void assertCategoryOwnedBy(Long categoryId, Long userId) {
        if (categoryId == null) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR);
        }
        CategoryDO c = categoryMapper.selectById(categoryId);
        if (c == null || c.getUserId() == null || !c.getUserId().equals(userId) || Boolean.TRUE.equals(c.getIsDeleted())) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR);
        }
    }

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = userDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }

    private ArticleDO getOwnArticle(Long articleId) {
        ArticleDO articleDO = articleDao.queryByArticleId(articleId, getCurrentUser().getId());
        if (articleDO == null) {
            throw new BizException(ResponseCodeEnum.ARTICLE_ACCESS_DENIED);
        }
        return articleDO;
    }

    private String normalizeVisibility(String visibility) {
        if (ArticleVisibilityConstants.PRIVATE.equalsIgnoreCase(visibility)) {
            return ArticleVisibilityConstants.PRIVATE;
        }
        return ArticleVisibilityConstants.PUBLIC;
    }

}
