package com.quanxiaoha.weblog.web.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendConfigDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendFeedbackDO;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorLogDO;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendConfigMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendFeedbackMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserBehaviorLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserProfileMapper;
import com.quanxiaoha.weblog.web.dao.RecommendDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RecommendDaoImpl implements RecommendDao {

    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RecommendFeedbackMapper recommendFeedbackMapper;

    @Autowired
    private RecommendConfigMapper recommendConfigMapper;

    @Override
    public int insertBehaviorLog(UserBehaviorLogDO behaviorLogDO) {
        return userBehaviorLogMapper.insert(behaviorLogDO);
    }

    @Override
    public int batchInsertBehaviorLogs(List<UserBehaviorLogDO> behaviorLogDOs) {
        int count = 0;
        for (UserBehaviorLogDO logDO : behaviorLogDOs) {
            count += userBehaviorLogMapper.insert(logDO);
        }
        return count;
    }

    @Override
    public List<UserBehaviorLogDO> queryRecentBehaviors(Long userId, int limit) {
        LambdaQueryWrapper<UserBehaviorLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehaviorLogDO::getUserId, userId)
                .orderByDesc(UserBehaviorLogDO::getCreateTime)
                .last("limit " + limit);
        return userBehaviorLogMapper.selectList(wrapper);
    }

    @Override
    public UserProfileDO queryUserProfile(Long userId) {
        LambdaQueryWrapper<UserProfileDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfileDO::getUserId, userId)
                .last("limit 1");
        return userProfileMapper.selectOne(wrapper);
    }

    @Override
    public int insertOrUpdateUserProfile(UserProfileDO userProfileDO) {
        UserProfileDO existing = queryUserProfile(userProfileDO.getUserId());
        if (existing == null) {
            return userProfileMapper.insert(userProfileDO);
        }
        userProfileDO.setId(existing.getId());
        return userProfileMapper.updateById(userProfileDO);
    }

    @Override
    public List<ArticleDO> queryHotArticles(int limit) {
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getIsDeleted, false)
                .orderByDesc(ArticleDO::getReadNum)
                .last("limit " + limit);
        return articleMapper.selectList(wrapper);
    }

    @Override
    public List<ArticleDO> queryArticlesByTagIds(List<Long> tagIds, int limit) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getIsDeleted, false)
                .inSql(ArticleDO::getId,
                        "SELECT article_id FROM t_article_tag_rel WHERE tag_id IN ("
                                + tagIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("0")
                                + ")")
                .orderByDesc(ArticleDO::getCreateTime)
                .last("limit " + limit);
        return articleMapper.selectList(wrapper);
    }

    @Override
    public List<ArticleDO> queryArticlesByCategoryId(Long categoryId, int limit) {
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getIsDeleted, false)
                .inSql(ArticleDO::getId,
                        "SELECT article_id FROM t_article_category_rel WHERE category_id = " + categoryId)
                .orderByDesc(ArticleDO::getCreateTime)
                .last("limit " + limit);
        return articleMapper.selectList(wrapper);
    }

    @Override
    public int insertFeedback(RecommendFeedbackDO feedbackDO) {
        return recommendFeedbackMapper.insert(feedbackDO);
    }

    @Override
    public RecommendConfigDO queryRecommendConfig(String configKey) {
        LambdaQueryWrapper<RecommendConfigDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendConfigDO::getConfigKey, configKey)
                .last("limit 1");
        return recommendConfigMapper.selectOne(wrapper);
    }

    @Override
    public int updateRecommendConfig(RecommendConfigDO configDO) {
        return recommendConfigMapper.updateById(configDO);
    }

    @Override
    public List<RecommendConfigDO> queryAllConfigs() {
        return recommendConfigMapper.selectList(null);
    }
}
