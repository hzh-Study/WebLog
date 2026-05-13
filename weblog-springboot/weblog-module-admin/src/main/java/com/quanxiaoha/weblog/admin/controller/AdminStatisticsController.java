package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.dashboard.ArticleRankRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.CategoryRatioRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.DashboardOverviewRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PublishHeatmapRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PvUvTrendReqVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PvUvTrendRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.TagRatioRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.VisitorRegionRspVO;
import com.quanxiaoha.weblog.admin.service.AdminStatisticsService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    @PostMapping("/overview")
    @ApiOperationLog(description = "\u83b7\u53d6\u4eea\u8868\u76d8\u7efc\u5408\u7edf\u8ba1\u4fe1\u606f")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<DashboardOverviewRspVO> getOverview() {
        return statisticsService.getOverview();
    }

    @PostMapping("/pvuv/trend")
    @ApiOperationLog(description = "\u83b7\u53d6 PV/UV \u8d8b\u52bf\u6570\u636e")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<PvUvTrendRspVO> getPvUvTrend(@RequestBody PvUvTrendReqVO reqVO) {
        return statisticsService.getPvUvTrend(reqVO);
    }

    @PostMapping("/article/rank")
    @ApiOperationLog(description = "\u83b7\u53d6\u6587\u7ae0\u70ed\u5ea6\u6392\u884c\u699c")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<ArticleRankRspVO> getArticleRank() {
        return statisticsService.getArticleRank();
    }

    @PostMapping("/visitor/region")
    @ApiOperationLog(description = "\u83b7\u53d6\u8bbf\u5ba2\u5730\u57df\u5206\u5e03")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<VisitorRegionRspVO> getVisitorRegion() {
        return statisticsService.getVisitorRegion();
    }

    @PostMapping("/publish/heatmap")
    @ApiOperationLog(description = "\u83b7\u53d6\u53d1\u5e03\u70ed\u529b\u56fe\u6570\u636e")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<PublishHeatmapRspVO> getPublishHeatmap() {
        return statisticsService.getPublishHeatmap();
    }

    @PostMapping("/category/ratio")
    @ApiOperationLog(description = "\u83b7\u53d6\u5206\u7c7b\u5360\u6bd4")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<CategoryRatioRspVO> getCategoryRatio() {
        return statisticsService.getCategoryRatio();
    }

    @PostMapping("/tag/ratio")
    @ApiOperationLog(description = "\u83b7\u53d6\u6807\u7b7e\u5360\u6bd4")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<TagRatioRspVO> getTagRatio() {
        return statisticsService.getTagRatio();
    }
}