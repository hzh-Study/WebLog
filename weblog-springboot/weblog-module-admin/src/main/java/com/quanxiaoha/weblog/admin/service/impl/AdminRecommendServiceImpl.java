package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.dao.AdminRecommendDao;
import com.quanxiaoha.weblog.admin.model.vo.recommend.QueryRecommendConfigRspVO;
import com.quanxiaoha.weblog.admin.model.vo.recommend.QueryRecommendDashboardRspVO;
import com.quanxiaoha.weblog.admin.model.vo.recommend.UpdateRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.service.AdminRecommendService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.RecommendConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminRecommendServiceImpl implements AdminRecommendService {

    @Autowired
    private AdminRecommendDao adminRecommendDao;

    @Override
    public Response queryAllConfigs() {
        List<RecommendConfigDO> configs = adminRecommendDao.queryAllConfigs();
        List<QueryRecommendConfigRspVO> voList = configs.stream()
                .map(config -> QueryRecommendConfigRspVO.builder()
                        .id(config.getId())
                        .configKey(config.getConfigKey())
                        .configValue(config.getConfigValue())
                        .description(config.getDescription())
                        .build())
                .collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public Response updateConfig(UpdateRecommendConfigReqVO reqVO) {
        RecommendConfigDO configDO = RecommendConfigDO.builder()
                .id(reqVO.getId())
                .configValue(reqVO.getConfigValue())
                .build();
        adminRecommendDao.updateConfig(configDO);
        return Response.success();
    }

    @Override
    public Response queryDashboard() {
        Long totalExposures = adminRecommendDao.countExposuresToday();
        Long totalClicks = adminRecommendDao.countClicksToday();
        Long totalFeedbacks = adminRecommendDao.countFeedbacksToday();
        Long activeUserCount = adminRecommendDao.countActiveUsers();

        Double avgCtr = 0.0;
        if (totalExposures != null && totalExposures > 0 && totalClicks != null) {
            avgCtr = totalClicks.doubleValue() / totalExposures.doubleValue();
        }

        QueryRecommendDashboardRspVO vo = QueryRecommendDashboardRspVO.builder()
                .totalExposures(totalExposures != null ? totalExposures : 0L)
                .totalClicks(totalClicks != null ? totalClicks : 0L)
                .avgCtr(avgCtr)
                .totalFeedbacks(totalFeedbacks != null ? totalFeedbacks : 0L)
                .activeUserCount(activeUserCount != null ? activeUserCount : 0L)
                .build();
        return Response.success(vo);
    }
}
