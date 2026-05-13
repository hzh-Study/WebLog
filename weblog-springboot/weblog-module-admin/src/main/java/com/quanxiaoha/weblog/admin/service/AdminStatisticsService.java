package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.dashboard.*;
import com.quanxiaoha.weblog.common.Response;

public interface AdminStatisticsService {

    Response<DashboardOverviewRspVO> getOverview();

    Response<PvUvTrendRspVO> getPvUvTrend(PvUvTrendReqVO reqVO);

    Response<ArticleRankRspVO> getArticleRank();

    Response<VisitorRegionRspVO> getVisitorRegion();

    Response<PublishHeatmapRspVO> getPublishHeatmap();

    Response<CategoryRatioRspVO> getCategoryRatio();

    Response<TagRatioRspVO> getTagRatio();
}