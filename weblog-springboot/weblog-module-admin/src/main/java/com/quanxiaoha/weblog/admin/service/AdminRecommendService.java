package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.admin.model.vo.recommend.UpdateRecommendConfigReqVO;

public interface AdminRecommendService {

    Response queryAllConfigs();

    Response updateConfig(UpdateRecommendConfigReqVO updateRecommendConfigReqVO);

    Response queryDashboard();
}
