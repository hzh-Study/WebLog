package com.quanxiaoha.weblog.web.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendConfigDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendFeedbackDO;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorLogDO;
import com.quanxiaoha.weblog.common.domain.dos.UserProfileDO;

import java.util.List;

public interface RecommendDao {
    int insertBehaviorLog(UserBehaviorLogDO behaviorLogDO);

    int batchInsertBehaviorLogs(List<UserBehaviorLogDO> behaviorLogDOs);

    List<UserBehaviorLogDO> queryRecentBehaviors(Long userId, int limit);

    UserProfileDO queryUserProfile(Long userId);

    int insertOrUpdateUserProfile(UserProfileDO userProfileDO);

    List<ArticleDO> queryHotArticles(int limit);

    List<ArticleDO> queryArticlesByTagIds(List<Long> tagIds, int limit);

    List<ArticleDO> queryArticlesByCategoryId(Long categoryId, int limit);

    int insertFeedback(RecommendFeedbackDO feedbackDO);

    RecommendConfigDO queryRecommendConfig(String configKey);

    int updateRecommendConfig(RecommendConfigDO configDO);

    List<RecommendConfigDO> queryAllConfigs();
}
