package com.quanxiaoha.weblog.admin.async;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.*;
import com.quanxiaoha.weblog.common.domain.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendScheduledTask {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleHotScoreMapper articleHotScoreMapper;
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Scheduled(cron = "0 0 * * * ?")
    public void updateArticleHotScores() {
        log.info("## 开始更新文章热度分...");
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC);
        List<ArticleDO> articles = articleMapper.selectList(wrapper);
        if (articles.isEmpty()) {
            log.info("## 无公开文章，跳过热度分更新");
            return;
        }

        Date now = new Date();
        for (ArticleDO article : articles) {
            long readNum = article.getReadNum() != null ? article.getReadNum() : 0L;
            long likeNum = article.getLikeNum() != null ? article.getLikeNum() : 0L;
            long favoriteNum = article.getFavoriteNum() != null ? article.getFavoriteNum() : 0L;

            long daysSincePublish = 0;
            if (article.getCreateTime() != null) {
                long diffMillis = now.getTime() - article.getCreateTime().getTime();
                daysSincePublish = TimeUnit.MILLISECONDS.toDays(diffMillis);
            }

            double decayFactor = Math.exp(-0.1 * daysSincePublish);
            double hotScore = readNum * 1.0 + likeNum * 3.0 + favoriteNum * 5.0;
            hotScore = hotScore * decayFactor;

            LambdaQueryWrapper<ArticleHotScoreDO> scoreWrapper = new LambdaQueryWrapper<>();
            scoreWrapper.eq(ArticleHotScoreDO::getArticleId, article.getId());
            ArticleHotScoreDO existing = articleHotScoreMapper.selectOne(scoreWrapper);

            if (existing != null) {
                existing.setHotScore(BigDecimal.valueOf(hotScore));
                existing.setCalculatedAt(now);
                articleHotScoreMapper.updateById(existing);
            } else {
                ArticleHotScoreDO newScore = ArticleHotScoreDO.builder()
                        .articleId(article.getId())
                        .hotScore(BigDecimal.valueOf(hotScore))
                        .readCount24h(0)
                        .interactionCount24h(0)
                        .calculatedAt(now)
                        .build();
                articleHotScoreMapper.insert(newScore);
            }
        }
        log.info("## 文章热度分更新完成，共处理 {} 篇文章", articles.size());
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void updateUserProfiles() {
        log.info("## 开始更新用户画像...");
        QueryWrapper<UserBehaviorLogDO> userWrapper = new QueryWrapper<>();
        userWrapper.select("DISTINCT user_id").isNotNull("user_id");
        List<Map<String, Object>> userMaps = userBehaviorLogMapper.selectMaps(userWrapper);
        if (userMaps == null || userMaps.isEmpty()) {
            log.info("## 无有行为记录的用户，跳过画像更新");
            return;
        }

        for (Map<String, Object> userMap : userMaps) {
            Object userIdObj = userMap.get("user_id");
            if (userIdObj == null) {
                continue;
            }
            Long userId = ((Number) userIdObj).longValue();

            LambdaQueryWrapper<UserBehaviorLogDO> behaviorWrapper = new LambdaQueryWrapper<>();
            behaviorWrapper.eq(UserBehaviorLogDO::getUserId, userId)
                    .eq(UserBehaviorLogDO::getEventType, "article_view")
                    .orderByDesc(UserBehaviorLogDO::getCreateTime)
                    .last("limit 100");
            List<UserBehaviorLogDO> behaviors = userBehaviorLogMapper.selectList(behaviorWrapper);

            if (behaviors.isEmpty()) {
                continue;
            }

            List<Long> articleIds = behaviors.stream()
                    .map(UserBehaviorLogDO::getTargetId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (articleIds.isEmpty()) {
                continue;
            }

            LambdaQueryWrapper<ArticleTagRelDO> tagRelWrapper = new LambdaQueryWrapper<>();
            tagRelWrapper.in(ArticleTagRelDO::getArticleId, articleIds);
            List<ArticleTagRelDO> tagRels = articleTagRelMapper.selectList(tagRelWrapper);

            Map<Long, Long> tagCountMap = tagRels.stream()
                    .collect(Collectors.groupingBy(ArticleTagRelDO::getTagId, Collectors.counting()));

            List<Map.Entry<Long, Long>> sortedTags = tagCountMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            List<String> tagNames = new ArrayList<>();
            if (!sortedTags.isEmpty()) {
                Set<Long> tagIds = sortedTags.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
                List<TagDO> tags = tagMapper.selectBatchIds(tagIds);
                Map<Long, String> tagNameMap = tags.stream()
                        .collect(Collectors.toMap(TagDO::getId, TagDO::getName, (a, b) -> a));

                for (Map.Entry<Long, Long> entry : sortedTags) {
                    String name = tagNameMap.get(entry.getKey());
                    if (name != null) {
                        tagNames.add(name);
                    }
                }
            }

            String interestTagsJson = "[]";
            if (!tagNames.isEmpty()) {
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < tagNames.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append("\"").append(tagNames.get(i).replace("\"", "\\\"")).append("\"");
                }
                sb.append("]");
                interestTagsJson = sb.toString();
            }

            LambdaQueryWrapper<UserProfileDO> profileWrapper = new LambdaQueryWrapper<>();
            profileWrapper.eq(UserProfileDO::getUserId, userId);
            UserProfileDO existingProfile = userProfileMapper.selectOne(profileWrapper);

            Date now = new Date();
            if (existingProfile != null) {
                existingProfile.setInterestTags(interestTagsJson);
                existingProfile.setUpdateTime(now);
                userProfileMapper.updateById(existingProfile);
            } else {
                UserProfileDO newProfile = UserProfileDO.builder()
                        .userId(userId)
                        .interestTags(interestTagsJson)
                        .personalizedEnabled(true)
                        .createTime(now)
                        .updateTime(now)
                        .build();
                userProfileMapper.insert(newProfile);
            }
        }
        log.info("## 用户画像更新完成，共处理 {} 个用户", userMaps.size());
    }
}
