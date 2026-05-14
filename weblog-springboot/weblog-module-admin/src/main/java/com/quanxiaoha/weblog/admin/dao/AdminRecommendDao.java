package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.RecommendConfigDO;

import java.util.List;

public interface AdminRecommendDao {
    List<RecommendConfigDO> queryAllConfigs();

    int updateConfig(RecommendConfigDO configDO);

    Long countExposuresToday();

    Long countClicksToday();

    Long countFeedbacksToday();

    Long countActiveUsers();
}
