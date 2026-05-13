package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.quanxiaoha.weblog.admin.dao.*;
import com.quanxiaoha.weblog.admin.model.vo.article.*;
import com.quanxiaoha.weblog.admin.service.AdminArticleService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.AiArticleConstants;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 鐘皬鍝?
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
    @Autowired
    private AdminArticleVersionDao articleVersionDao;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AdminArticleServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        UserDO currentUser = getCurrentUser();
        Long categoryId = resolveCategoryId(publishArticleReqVO, currentUser.getId());
        final Long[] articleIdHolder = new Long[1];
        final String[] titleHolder = new String[1];
        final String[] descHolder = new String[1];
        final String[] contentHolder = new String[1];
        boolean isExecuteSuccess = transactionTemplate.execute(status -> {
            ArticleDO articleDO = ArticleDO.builder()
                    .userId(currentUser.getId())
                    .title(publishArticleReqVO.getTitle())
                    .titleImage(normalizeTitleImage(publishArticleReqVO.getTitleImage()))
                    .description(defaultIfEmpty(publishArticleReqVO.getDescription(),
                            extractDescription(publishArticleReqVO.getContent())))
                    .visibility(normalizeVisibility(publishArticleReqVO.getVisibility()))
                    .build();
            articleDao.insertArticle(articleDO);

            Long articleId = articleDO.getId();
            articleIdHolder[0] = articleId;
            titleHolder[0] = articleDO.getTitle();
            descHolder[0] = articleDO.getDescription();
            contentHolder[0] = publishArticleReqVO.getContent();

            ArticleContentDO articleContentDO = ArticleContentDO.builder()
                    .articleId(articleId)
                    .content(publishArticleReqVO.getContent())
                    .build();
            articleContentDao.insertArticleContent(articleContentDO);

            if (categoryId != null) {
                ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                        .articleId(articleId)
                        .categoryId(categoryId)
                        .build();
                articleCategoryRelDao.insert(articleCategoryRelDO);
            }

            List<String> publishTags = publishArticleReqVO.getTags();
            if (!CollectionUtils.isEmpty(publishTags)) {
                handleTagBiz(articleId, publishTags, currentUser.getId());
            }
            return true;
        });

        if (isExecuteSuccess && articleIdHolder[0] != null) {
            ArticleDO snapshotDO = ArticleDO.builder()
                    .id(articleIdHolder[0])
                    .title(titleHolder[0])
                    .description(descHolder[0])
                    .build();
            createVersionSnapshot(snapshotDO, contentHolder[0], currentUser.getId(), "鏂囩珷鍙戝竷");
        }
        return isExecuteSuccess ? Response.success() : Response.fail();
    }

    @Override
    public Response queryArticleDetail(QueryArticleDetailReqVO queryArticleDetailReqVO) {
        Long articleId = queryArticleDetailReqVO.getArticleId();
        ArticleDO articleDO = getOwnArticle(articleId);
        ArticleContentDO articleContentDO = articleContentDao.queryByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelDao.selectByArticleId(articleId);

        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelDao.selectByArticleId(articleId);
        List<Long> tagIds = CollectionUtils.isEmpty(articleTagRelDOS)
                ? Collections.emptyList()
                : articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

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
        articleCategoryRelDao.deleteByArticleId(articleId);
        articleTagRelDao.deleteByArticleId(articleId);
        return Response.success();
    }

    @Override
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        assertCategoryOwnedBy(updateArticleReqVO.getCategoryId(), getCurrentUser().getId());
        final Long[] articleIdHolder = new Long[1];
        final String[] titleHolder = new String[1];
        final String[] descHolder = new String[1];
        final String[] contentHolder = new String[1];
        boolean isExecuteSuccess = transactionTemplate.execute(status -> {
            Long articleId = updateArticleReqVO.getId();
            articleIdHolder[0] = articleId;
            titleHolder[0] = updateArticleReqVO.getTitle();
            descHolder[0] = updateArticleReqVO.getDescription();
            contentHolder[0] = updateArticleReqVO.getContent();
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
            int contentRows = articleContentDao.updateByArticleId(articleContentDO);
            if (contentRows == 0) {
                articleContentDao.insertArticleContent(articleContentDO);
            }

            articleCategoryRelDao.deleteByArticleId(articleId);
            ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                    .articleId(articleId)
                    .categoryId(updateArticleReqVO.getCategoryId())
                    .build();
            articleCategoryRelDao.insert(articleCategoryRelDO);

            articleTagRelDao.deleteByArticleId(articleId);
            handleTagBiz(articleId, updateArticleReqVO.getTags(), currentUser.getId());
            return true;
        });

        if (isExecuteSuccess && articleIdHolder[0] != null) {
            ArticleDO snapshotDO = ArticleDO.builder()
                    .id(articleIdHolder[0])
                    .title(titleHolder[0])
                    .description(descHolder[0])
                    .build();
            createVersionSnapshot(snapshotDO, contentHolder[0], getCurrentUser().getId(), "鏂囩珷鏇存柊");
        }
        return isExecuteSuccess ? Response.success() : Response.fail();
    }

    private void createVersionSnapshot(ArticleDO articleDO, String content, Long userId, String changeSummary) {
        try {
            Integer maxVer = articleVersionDao.selectMaxVersionNum(articleDO.getId());
            int newVer = (maxVer != null ? maxVer : 0) + 1;
            ArticleVersionDO version = ArticleVersionDO.builder()
                    .articleId(articleDO.getId())
                    .userId(userId)
                    .title(articleDO.getTitle() != null ? articleDO.getTitle() : "")
                    .content(content != null ? content : "")
                    .description(articleDO.getDescription())
                    .changeSummary(changeSummary)
                    .versionNum(newVer)
                    .createTime(new Date())
                    .build();
            articleVersionDao.insert(version);
            articleVersionDao.deleteOldestExceed(articleDO.getId(), 50);
        } catch (Exception e) {
            log.error("鍒涘缓鐗堟湰蹇収澶辫触: articleId={}", articleDO.getId(), e);
        }
    }

    public void handleTagBiz(Long articleId, List<String> publishTags, Long userId) {
        if (CollectionUtils.isEmpty(publishTags)) {
            return;
        }

        List<TagDO> tagDOS = tagDao.selectAllByUserId(userId);
        Map<String, Long> existedTagIdMap = new HashMap<>();
        Map<String, Long> existedTagNameMap = new HashMap<>();
        for (TagDO tagDO : tagDOS) {
            existedTagIdMap.put(String.valueOf(tagDO.getId()), tagDO.getId());
            if (tagDO.getName() != null) {
                existedTagNameMap.put(tagDO.getName().trim(), tagDO.getId());
            }
        }

        LinkedHashSet<Long> relationTagIds = new LinkedHashSet<>();
        List<String> newTagNames = Lists.newArrayList();
        for (String rawTag : publishTags) {
            String tagValue = rawTag == null ? "" : rawTag.trim();
            if (tagValue.isEmpty()) {
                continue;
            }

            Long existedTagId = existedTagIdMap.get(tagValue);
            if (existedTagId != null) {
                relationTagIds.add(existedTagId);
                continue;
            }

            Long existedTagName = existedTagNameMap.get(tagValue);
            if (existedTagName != null) {
                relationTagIds.add(existedTagName);
                continue;
            }

            if (!newTagNames.contains(tagValue)) {
                newTagNames.add(tagValue);
            }
        }

        for (String newTagName : newTagNames) {
            TagDO tagDO = TagDO.builder()
                    .userId(userId)
                    .name(newTagName)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isDeleted(false)
                    .build();
            tagDao.insert(tagDO);
            relationTagIds.add(tagDO.getId());
        }

        if (!relationTagIds.isEmpty()) {
            List<ArticleTagRelDO> articleTagRelDOS = relationTagIds.stream()
                    .map(tagId -> ArticleTagRelDO.builder()
                            .articleId(articleId)
                            .tagId(tagId)
                            .build())
                    .collect(Collectors.toList());
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

    private Long resolveCategoryId(PublishArticleReqVO reqVO, Long userId) {
        if (reqVO.getCategoryId() != null) {
            assertCategoryOwnedBy(reqVO.getCategoryId(), userId);
            return reqVO.getCategoryId();
        }
        if (reqVO.getCategoryName() != null && !reqVO.getCategoryName().trim().isEmpty()) {
            String name = reqVO.getCategoryName().trim();
            CategoryDO existing = categoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CategoryDO>()
                            .eq(CategoryDO::getUserId, userId)
                            .eq(CategoryDO::getName, name)
                            .eq(CategoryDO::getIsDeleted, false)
            );
            if (existing != null) {
                return existing.getId();
            }
            CategoryDO newCat = CategoryDO.builder()
                    .userId(userId)
                    .name(name)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isDeleted(false)
                    .build();
            categoryMapper.insert(newCat);
            return newCat.getId();
        }
        return null;
    }

    private String defaultIfEmpty(String value, String defaultValue) {
        return value != null && !value.trim().isEmpty() ? value : defaultValue;
    }

    private String normalizeTitleImage(String titleImage) {
        return defaultIfEmpty(titleImage, AiArticleConstants.DEFAULT_SCENERY_COVER);
    }

    private String extractDescription(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        String plain = content.replaceAll("<[^>]+>", "")
                .replaceAll("#+\\s*", "")
                .replaceAll("\\*\\*|__", "")
                .replaceAll("\\*|_", "")
                .replaceAll("`", "")
                .replaceAll("\\n+", " ")
                .trim();
        return plain.length() > 200 ? plain.substring(0, 200) + "..." : plain;
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
