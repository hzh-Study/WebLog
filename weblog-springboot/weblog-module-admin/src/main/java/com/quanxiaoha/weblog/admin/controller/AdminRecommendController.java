package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.recommend.UpdateRecommendConfigReqVO;
import com.quanxiaoha.weblog.admin.service.AdminRecommendService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/recommend")
public class AdminRecommendController {

    @Autowired
    private AdminRecommendService adminRecommendService;

    @PostMapping("/config/list")
    @ApiOperationLog(description = "查询所有推荐配置")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response queryAllConfigs() {
        return adminRecommendService.queryAllConfigs();
    }

    @PostMapping("/config/update")
    @ApiOperationLog(description = "更新推荐配置")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateConfig(@RequestBody @Validated UpdateRecommendConfigReqVO reqVO) {
        return adminRecommendService.updateConfig(reqVO);
    }

    @PostMapping("/dashboard")
    @ApiOperationLog(description = "查询推荐监控数据")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response queryDashboard() {
        return adminRecommendService.queryDashboard();
    }
}
