package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleTagRelDO;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendFeedbackDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorLogDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import com.quanxiaoha.weblog.web.dao.ArticleDao;
import com.quanxiaoha.weblog.web.dao.RecommendDao;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryHotArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRecommendFeedReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRecommendFeedRspVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRecommendSettingsRspVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRelatedArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitFeedbackReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.UpdateRecommendSettingsReqVO;
import com.quanxiaoha.weblog.web.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendDao recommendDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ObjectMapper objectMapper;

    private final LoadingCache<String, List<QueryRecommendFeedRspVO>> recommendCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build(new CacheLoader<String, List<QueryRecommendFeedRspVO>>() {
                @Override
                public List<QueryRecommendFeedRspVO> load(String key) {
                    return Collections.emptyList();
                }
            });

    @Override
    public PageResponse queryRecommendFeed(QueryRecommendFeedReqVO reqVO) {
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();
        String strategy = reqVO.getStrategy();
        Long userId = resolveCurrentUserId();

        String cacheKey = userId + "_" + strategy + "_" + current + "_" + size;
        List<QueryRecommendFeedRspVO> cached = recommendCache.getIfPresent(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return buildPageResponse(current, size, cached);
        }

        List<ArticleDO> articles;
        if (userId != null) {
            UserProfileDO userProfile = recommendDao.queryUserProfile(userId);
            if (userProfile != null && Boolean.TRUE.equals(userProfile.getPersonalizedEnabled())) {
                List<String> interestTags = parseInterestTags(userProfile.getInterestTags());
                articles = buildPersonalizedFeed(interestTags, (int) (current * size));
            } else {
                articles = recommendDao.queryHotArticles((int) (current * size));
            }
        } else {
            articles = recommendDao.queryHotArticles((int) (current * size));
        }

        articles = articles.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .filter(a -> ArticleVisibilityConstants.PUBLIC.equals(a.getVisibility()))
                .collect(Collectors.toList());

        List<QueryRecommendFeedRspVO> voList = convertToFeedRspVOList(articles);

        if (!voList.isEmpty()) {
            recommendCache.put(cacheKey, voList);
        }

        return buildPageResponse(current, size, voList);
    }

    @Override
    public Response queryRelatedArticles(QueryRelatedArticleReqVO reqVO) {
        Long articleId = reqVO.getArticleId();
        Integer limit = reqVO.getLimit();

        ArticleDO currentArticle = articleDao.selectArticleById(articleId);
        if (currentArticle == null) {
            return Response.success(Collections.emptyList());
        }

        LambdaQueryWrapper<ArticleTagRelDO> tagRelWrapper = new LambdaQueryWrapper<>();
        tagRelWrapper.eq(ArticleTagRelDO::getArticleId, articleId);
        List<ArticleTagRelDO> tagRels = articleTagRelMapper.selectList(tagRelWrapper);

        List<Long> tagIds = tagRels.stream()
                .map(ArticleTagRelDO::getTagId)
                .collect(Collectors.toList());

        List<ArticleDO> relatedArticles;
        if (!CollectionUtils.isEmpty(tagIds)) {
            relatedArticles = recommendDao.queryArticlesByTagIds(tagIds, limit);
        } else {
            LambdaQueryWrapper<ArticleCategoryRelDO> categoryRelWrapper = new LambdaQueryWrapper<>();
            categoryRelWrapper.eq(ArticleCategoryRelDO::getArticleId, articleId);
            ArticleCategoryRelDO categoryRel = articleCategoryRelMapper.selectOne(categoryRelWrapper);
            if (categoryRel != null) {
                relatedArticles = recommendDao.queryArticlesByCategoryId(categoryRel.getCategoryId(), limit);
            } else {
                relatedArticles = recommendDao.queryHotArticles(limit);
            }
        }

        relatedArticles = relatedArticles.stream()
                .filter(a -> !a.getId().equals(articleId))
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .filter(a -> ArticleVisibilityConstants.PUBLIC.equals(a.getVisibility()))
                .limit(limit)
                .collect(Collectors.toList());

        List<QueryRecommendFeedRspVO> voList = convertToFeedRspVOList(relatedArticles);
        return Response.success(voList);
    }

    @Override
    public Response queryHotArticles(QueryHotArticleReqVO reqVO) {
        Integer limit = reqVO.getLimit();
        List<ArticleDO> articles = recommendDao.queryHotArticles(limit);

        articles = articles.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .filter(a -> ArticleVisibilityConstants.PUBLIC.equals(a.getVisibility()))
                .collect(Collectors.toList());

        List<QueryRecommendFeedRspVO> voList = convertToFeedRspVOList(articles);
        return Response.success(voList);
    }

    @Override
    public Response submitBehavior(SubmitBehaviorReqVO reqVO) {
        Long userId = resolveCurrentUserId();

        UserBehaviorLogDO behaviorLogDO = UserBehaviorLogDO.builder()
                .userId(userId)
                .eventType(reqVO.getEventType())
                .targetId(reqVO.getTargetId())
                .targetType(reqVO.getTargetType())
                .eventData(reqVO.getEventData())
                .duration(reqVO.getDuration())
                .referrer(reqVO.getReferrer())
                .createTime(new Date())
                .build();

        recommendDao.insertBehaviorLog(behaviorLogDO);
        return Response.success();
    }

    @Override
    public Response submitFeedback(SubmitFeedbackReqVO reqVO) {
        Long userId = resolveCurrentUserId();

        RecommendFeedbackDO feedbackDO = RecommendFeedbackDO.builder()
                .userId(userId)
                .articleId(reqVO.getArticleId())
                .feedbackType(reqVO.getFeedbackType())
                .scene(reqVO.getScene())
                .createTime(new Date())
                .build();

        recommendDao.insertFeedback(feedbackDO);
        return Response.success();
    }

    @Override
    public Response queryRecommendSettings() {
        Long userId = resolveCurrentUserId();
        if (userId == null) {
            return Response.fail("用户未登录");
        }

        UserProfileDO userProfile = recommendDao.queryUserProfile(userId);
        if (userProfile == null) {
            return Response.success(QueryRecommendSettingsRspVO.builder()
                    .personalizedEnabled(true)
                    .interestTags(Collections.emptyList())
                    .build());
        }

        List<String> interestTags = parseInterestTags(userProfile.getInterestTags());
        return Response.success(QueryRecommendSettingsRspVO.builder()
                .personalizedEnabled(userProfile.getPersonalizedEnabled())
                .interestTags(interestTags)
                .build());
    }

    @Override
    public Response updateRecommendSettings(UpdateRecommendSettingsReqVO reqVO) {
        Long userId = resolveCurrentUserId();
        if (userId == null) {
            return Response.fail("用户未登录");
        }

        String interestTagsJson;
        try {
            interestTagsJson = reqVO.getInterestTags() != null
                    ? objectMapper.writeValueAsString(reqVO.getInterestTags())
                    : "[]";
        } catch (Exception e) {
            log.error("序列化兴趣标签失败", e);
            interestTagsJson = "[]";
        }

        UserProfileDO userProfileDO = UserProfileDO.builder()
                .userId(userId)
                .personalizedEnabled(reqVO.getPersonalizedEnabled())
                .interestTags(interestTagsJson)
                .updateTime(new Date())
                .build();

        recommendDao.insertOrUpdateUserProfile(userProfileDO);

        recommendCache.invalidateAll();

        return Response.success();
    }

    private List<ArticleDO> buildPersonalizedFeed(List<String> interestTags, int limit) {
        if (CollectionUtils.isEmpty(interestTags)) {
            return recommendDao.queryHotArticles(limit);
        }

        List<Long> tagIds = resolveTagIdsByNames(interestTags);
        int tagBasedLimit = (int) Math.ceil(limit * 0.6);
        int hotLimit = limit - tagBasedLimit;

        List<ArticleDO> tagBasedArticles = CollectionUtils.isEmpty(tagIds)
                ? Collections.emptyList()
                : recommendDao.queryArticlesByTagIds(tagIds, tagBasedLimit);
        List<ArticleDO> hotArticles = recommendDao.queryHotArticles(hotLimit);

        Set<Long> seenIds = new HashSet<>();
        List<ArticleDO> mixed = new ArrayList<>();
        for (ArticleDO a : tagBasedArticles) {
            if (seenIds.add(a.getId())) {
                mixed.add(a);
            }
        }
        for (ArticleDO a : hotArticles) {
            if (seenIds.add(a.getId())) {
                mixed.add(a);
            }
        }

        return mixed;
    }

    private List<Long> resolveTagIdsByNames(List<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TagDO::getName, tagNames)
                .eq(TagDO::getIsDeleted, false);
        List<TagDO> tags = tagMapper.selectList(wrapper);
        return tags.stream().map(TagDO::getId).collect(Collectors.toList());
    }

    private List<QueryRecommendFeedRspVO> convertToFeedRspVOList(List<ArticleDO> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return Collections.emptyList();
        }

        List<Long> articleIds = articles.stream().map(ArticleDO::getId).collect(Collectors.toList());
        List<Long> userIds = articles.stream().map(ArticleDO::getUserId).distinct().collect(Collectors.toList());

        Map<Long, ArticleCategoryRelDO> categoryRelMap = articleCategoryRelMapper.selectList(
                new LambdaQueryWrapper<ArticleCategoryRelDO>().in(ArticleCategoryRelDO::getArticleId, articleIds)
        ).stream().collect(Collectors.toMap(ArticleCategoryRelDO::getArticleId, rel -> rel, (a, b) -> a));

        Map<Long, List<ArticleTagRelDO>> articleTagMap = articleTagRelMapper.selectList(
                new LambdaQueryWrapper<ArticleTagRelDO>().in(ArticleTagRelDO::getArticleId, articleIds)
        ).stream().collect(Collectors.groupingBy(ArticleTagRelDO::getArticleId));

        Set<Long> categoryIdSet = categoryRelMap.values().stream()
                .map(ArticleCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> categoryIdNameMap = CollectionUtils.isEmpty(categoryIdSet)
                ? Collections.emptyMap()
                : categoryMapper.selectBatchIds(categoryIdSet).stream()
                    .collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName, (a, b) -> a));

        Set<Long> tagIdSet = articleTagMap.values().stream()
                .flatMap(List::stream)
                .map(ArticleTagRelDO::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> tagIdNameMap = CollectionUtils.isEmpty(tagIdSet)
                ? Collections.emptyMap()
                : tagMapper.selectBatchIds(tagIdSet).stream()
                    .collect(Collectors.toMap(TagDO::getId, TagDO::getName, (a, b) -> a));

        Map<Long, UserDO> authorMap = CollectionUtils.isEmpty(userIds)
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(UserDO::getId, user -> user, (a, b) -> a));

        return articles.stream().map(article -> {
            QueryRecommendFeedRspVO.QueryRecommendFeedRspVOBuilder builder = QueryRecommendFeedRspVO.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .description(article.getDescription())
                    .titleImage(article.getTitleImage())
                    .createTime(article.getCreateTime())
                    .readNum(article.getReadNum());

            ArticleCategoryRelDO categoryRel = categoryRelMap.get(article.getId());
            if (categoryRel != null) {
                builder.categoryName(categoryIdNameMap.get(categoryRel.getCategoryId()));
            }

            List<ArticleTagRelDO> tagRels = articleTagMap.getOrDefault(article.getId(), Collections.emptyList());
            List<String> tagNames = tagRels.stream()
                    .map(rel -> tagIdNameMap.get(rel.getTagId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            builder.tags(tagNames);

            UserDO author = authorMap.get(article.getUserId());
            if (author != null) {
                builder.author(author.getNickname() != null ? author.getNickname() : author.getUsername());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }

    private PageResponse buildPageResponse(Long current, Long size, List<QueryRecommendFeedRspVO> voList) {
        PageResponse<QueryRecommendFeedRspVO> response = new PageResponse<>();
        response.setSuccess(true);
        response.setCurrent(current);
        response.setSize(size);
        response.setTotal(voList.size());
        response.setPages(voList.isEmpty() ? 0 : (voList.size() + size - 1) / size);
        response.setData(voList);
        return response;
    }

    private Long resolveCurrentUserId() {
        try {
            String username = SecurityUtils.getCurrentUsername();
            if (username == null || "anonymousUser".equals(username)) {
                return null;
            }
            UserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                    .eq(UserDO::getUsername, username)
                    .eq(UserDO::getIsDeleted, false)
                    .last("limit 1"));
            return userDO == null ? null : userDO.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseInterestTags(String interestTagsJson) {
        if (!StringUtils.hasText(interestTagsJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(interestTagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析兴趣标签失败", e);
            return Collections.emptyList();
        }
    }
}
