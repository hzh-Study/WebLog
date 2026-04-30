package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.EventEnum;
import com.quanxiaoha.weblog.common.eventbus.ArticleEvent;
import com.quanxiaoha.weblog.common.exception.ResourceNotFoundException;
import com.quanxiaoha.weblog.web.convert.ArticleConvert;
import com.quanxiaoha.weblog.web.dao.*;
import com.quanxiaoha.weblog.web.model.vo.article.*;
import com.quanxiaoha.weblog.web.model.vo.category.QueryCategoryListItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.tag.QueryTagListItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.ArticleAuthorRspVO;
import com.quanxiaoha.weblog.web.service.ArticleService;
import com.quanxiaoha.weblog.web.utils.MarkdownUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleContentDao articleContentDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ArticleCategoryRelDao articleCategoryRelDao;
    @Autowired
    private ArticleTagRelDao articleTagRelDao;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private ArticleConvert articleConvert;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public PageResponse queryIndexArticlePageList(QueryIndexArticlePageListReqVO queryIndexArticlePageListReqVO) {
        Long current = queryIndexArticlePageListReqVO.getCurrent();
        Long size = queryIndexArticlePageListReqVO.getSize();
        String keyword = queryIndexArticlePageListReqVO.getKeyword();
        Long userId = resolveUserIdByUsername(queryIndexArticlePageListReqVO.getUsername());

        IPage<ArticleDO> articleDOIPage = articleDao.queryArticlePageList(current, size, keyword, userId);
        List<QueryIndexArticlePageItemRspVO> list = buildArticleList(articleDOIPage.getRecords(), keyword);
        return PageResponse.success(articleDOIPage, list);
    }

    @Override
    public PageResponse queryCategoryArticlePageList(QueryCategoryArticlePageListReqVO queryCategoryArticlePageListReqVO) {
        Long current = queryCategoryArticlePageListReqVO.getCurrent();
        Long size = queryCategoryArticlePageListReqVO.getSize();
        Long queryCategoryId = queryCategoryArticlePageListReqVO.getCategoryId();

        List<ArticleCategoryRelDO> articleCategoryRelDOList = articleCategoryRelDao.selectByCategoryId(queryCategoryId);

        // 判断该分类下是否存在文章
        if (CollectionUtils.isEmpty(articleCategoryRelDOList)) {
            return PageResponse.success(null, null);
        }

        List<Long> categoryArticleIds = articleCategoryRelDOList.stream().map(p -> p.getArticleId()).collect(Collectors.toList());

        IPage<ArticleDO> articleDOIPage = articleDao.queryArticlePageListByArticleIds(current, size, categoryArticleIds, null);
        List<QueryIndexArticlePageItemRspVO> list = buildArticleList(articleDOIPage.getRecords());
        return PageResponse.success(articleDOIPage, list);
    }

    @Override
    public Response queryArticleDetail(QueryArticleDetailReqVO queryArticleDetailReqVO) {
        Long articleId = queryArticleDetailReqVO.getArticleId();

        // 判断文章是否存在
        ArticleDO articleDO = articleDao.selectArticleById(articleId);

        if (Objects.isNull(articleDO)) {
            throw new ResourceNotFoundException();
        }

        ArticleContentDO articleContentDO = articleContentDao.selectArticleContentByArticleId(articleId);
        String rawContent = (articleContentDO == null) ? null : articleContentDO.getContent();

        QueryArticleDetailRspVO vo = QueryArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .updateTime(articleDO.getUpdateTime())
                .content(MarkdownUtil.parse2Html(rawContent == null ? "" : rawContent))
                .readNum(articleDO.getReadNum())
                .build();
        vo.setAuthor(buildAuthor(articleDO.getUserId()));

        // 查询文章所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelDao.selectByArticleId(articleId);
        if (articleCategoryRelDO != null) {
            CategoryDO categoryDO = categoryDao.selectByCategoryId(articleCategoryRelDO.getCategoryId());
            if (categoryDO != null) {
                vo.setCategoryId(categoryDO.getId());
                vo.setCategoryName(categoryDO.getName());
            }
        }

        // 查询文章标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelDao.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(p -> p.getTagId()).collect(Collectors.toList());
        List<TagDO> tagDOS;
        if (CollectionUtils.isEmpty(tagIds)) {
            tagDOS = Collections.emptyList();
        } else {
            tagDOS = new ArrayList<>(tagMapper.selectBatchIds(tagIds));
        }

        List<QueryTagListItemRspVO> queryTagListItemRspVOS = tagDOS.stream()
                .map(p -> QueryTagListItemRspVO.builder().id(p.getId()).name(p.getName()).build()).collect(Collectors.toList());
        vo.setTags(queryTagListItemRspVOS);

        // 上一篇
        ArticleDO preArticle = articleDao.selectPreArticle(articleId);
        if (Objects.nonNull(preArticle)) {
            QueryArticleLinkRspVO prevArticleVO = QueryArticleLinkRspVO.builder()
                    .title(preArticle.getTitle())
                    .id(preArticle.getId())
                    .build();
            vo.setPreArticle(prevArticleVO);
        }

        // 下一篇
        ArticleDO nextArticle = articleDao.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticle)) {
            QueryArticleLinkRspVO nextArticleVO = QueryArticleLinkRspVO.builder()
                    .title(nextArticle.getTitle())
                    .id(nextArticle.getId())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        // 发送消息事件
        log.info("发送 PV +1 消息事件");
        eventBus.post(ArticleEvent.builder().articleId(articleId).message(EventEnum.PV_INCREASE.getMessage()).build());

        return Response.success(vo);
    }

    @Override
    public PageResponse queryTagArticlePageList(QueryTagArticlePageListReqVO queryTagArticlePageListReqVO) {
        Long current = queryTagArticlePageListReqVO.getCurrent();
        Long size = queryTagArticlePageListReqVO.getSize();
        Long queryTagId = queryTagArticlePageListReqVO.getTagId();

        List<ArticleTagRelDO> articleTagRelDOS1 = articleTagRelDao.selectByTagId(queryTagId);

        // 判断该分类下是否存在文章
        if (CollectionUtils.isEmpty(articleTagRelDOS1)) {
            return PageResponse.success(null, null);
        }

        List<Long> tagArticleIds = articleTagRelDOS1.stream().map(p -> p.getArticleId()).collect(Collectors.toList());

        IPage<ArticleDO> articleDOIPage = articleDao.queryArticlePageListByArticleIds(current, size, tagArticleIds, null);
        List<QueryIndexArticlePageItemRspVO> list = buildArticleList(articleDOIPage.getRecords());
        return PageResponse.success(articleDOIPage, list);
    }

    private Long resolveUserIdByUsername(String username) {
        if (!org.springframework.util.StringUtils.hasText(username)) {
            return null;
        }
        UserDO userDO = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, username.trim())
                .eq(UserDO::getIsDeleted, false)
                .last("limit 1"));
        return userDO == null ? -1L : userDO.getId();
    }

    private List<QueryIndexArticlePageItemRspVO> buildArticleList(List<ArticleDO> records) {
        return buildArticleList(records, null);
    }

    private List<QueryIndexArticlePageItemRspVO> buildArticleList(List<ArticleDO> records, String keyword) {
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }

        List<QueryIndexArticlePageItemRspVO> list = records.stream()
                .map(articleConvert::convert)
                .collect(Collectors.toList());

        List<Long> articleIds = list.stream().map(QueryIndexArticlePageItemRspVO::getId).collect(Collectors.toList());
        List<Long> userIds = records.stream().map(ArticleDO::getUserId).distinct().collect(Collectors.toList());

        Map<Long, ArticleCategoryRelDO> categoryRelMap = articleCategoryRelDao.selectByArticleIds(articleIds).stream()
                .collect(Collectors.toMap(ArticleCategoryRelDO::getArticleId, rel -> rel, (a, b) -> a));
        Map<Long, List<ArticleTagRelDO>> articleTagMap = articleTagRelDao.selectByArticleIds(articleIds).stream()
                .collect(Collectors.groupingBy(ArticleTagRelDO::getArticleId));
        Set<Long> categoryIdSet = categoryRelMap.values().stream()
                .map(ArticleCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        final Map<Long, String> categoryIdNameMap;
        if (CollectionUtils.isEmpty(categoryIdSet)) {
            categoryIdNameMap = Collections.emptyMap();
        } else {
            categoryIdNameMap = categoryMapper.selectBatchIds(categoryIdSet).stream()
                    .collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName, (a, b) -> a));
        }
        Set<Long> tagIdSet = articleTagMap.values().stream()
                .flatMap(List::stream)
                .map(ArticleTagRelDO::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        final Map<Long, String> tagIdNameMap;
        if (CollectionUtils.isEmpty(tagIdSet)) {
            tagIdNameMap = Collections.emptyMap();
        } else {
            tagIdNameMap = tagMapper.selectBatchIds(tagIdSet).stream()
                    .collect(Collectors.toMap(TagDO::getId, TagDO::getName, (a, b) -> a));
        }
        Map<Long, UserDO> authorMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(UserDO::getId, user -> user, (a, b) -> a));
        Map<Long, String> contentMap;
        if (org.springframework.util.StringUtils.hasText(keyword)) {
            contentMap = articleContentDao.selectArticleContentByArticleIds(articleIds).stream()
                    .collect(Collectors.toMap(ArticleContentDO::getArticleId, ArticleContentDO::getContent, (a, b) -> a));
        } else {
            contentMap = Collections.emptyMap();
        }
        final Map<Long, String> snippetContentMap = contentMap;

        list.forEach(item -> {
            ArticleCategoryRelDO categoryRelDO = categoryRelMap.get(item.getId());
            if (categoryRelDO != null) {
                item.setCategory(QueryCategoryListItemRspVO.builder()
                        .id(categoryRelDO.getCategoryId())
                        .name(categoryIdNameMap.get(categoryRelDO.getCategoryId()))
                        .build());
            }

            List<QueryTagListItemRspVO> tagList = Lists.newArrayList();
            List<ArticleTagRelDO> relDOS = articleTagMap.getOrDefault(item.getId(), Lists.newArrayList());
            relDOS.forEach(rel -> tagList.add(QueryTagListItemRspVO.builder()
                    .id(rel.getTagId())
                    .name(tagIdNameMap.get(rel.getTagId()))
                    .build()));
            item.setTags(tagList);

            UserDO author = authorMap.get(records.stream()
                    .filter(record -> Objects.equals(record.getId(), item.getId()))
                    .findFirst()
                    .map(ArticleDO::getUserId)
                    .orElse(null));
            if (author != null) {
                item.setAuthor(ArticleAuthorRspVO.builder()
                        .id(author.getId())
                        .username(author.getUsername())
                        .nickname(author.getNickname())
                        .avatar(author.getAvatar())
                        .build());
            }

            if (org.springframework.util.StringUtils.hasText(keyword) && !containsKeyword(item.getTitle(), keyword) && !containsKeyword(item.getDescription(), keyword)) {
                item.setSearchSnippet(buildSearchSnippet(snippetContentMap.get(item.getId()), keyword));
            }
        });
        return list;
    }

    private boolean containsKeyword(String text, String keyword) {
        if (!org.springframework.util.StringUtils.hasText(text) || !org.springframework.util.StringUtils.hasText(keyword)) {
            return false;
        }
        return text.toLowerCase().contains(keyword.trim().toLowerCase());
    }

    private String buildSearchSnippet(String content, String keyword) {
        if (!org.springframework.util.StringUtils.hasText(content) || !org.springframework.util.StringUtils.hasText(keyword)) {
            return null;
        }

        String normalized = content.replaceAll("<[^>]+>", " ")
                .replaceAll("[#>*_`\\[\\]()!\\-]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        String lowerContent = normalized.toLowerCase();
        String lowerKeyword = keyword.trim().toLowerCase();
        int index = lowerContent.indexOf(lowerKeyword);
        if (index < 0) {
            return null;
        }

        int start = Math.max(0, index - 36);
        int end = Math.min(normalized.length(), index + keyword.trim().length() + 72);
        String prefix = start > 0 ? "..." : "";
        String suffix = end < normalized.length() ? "..." : "";
        return prefix + normalized.substring(start, end) + suffix;
    }

    private ArticleAuthorRspVO buildAuthor(Long userId) {
        if (userId == null) {
            return null;
        }
        UserDO author = userMapper.selectById(userId);
        if (author == null) {
            return null;
        }
        return ArticleAuthorRspVO.builder()
                .id(author.getId())
                .username(author.getUsername())
                .nickname(author.getNickname())
                .avatar(author.getAvatar())
                .build();
    }
}
