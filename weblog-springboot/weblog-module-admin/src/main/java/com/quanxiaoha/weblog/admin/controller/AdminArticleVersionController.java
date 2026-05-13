package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDetailReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDiffReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionRollbackReqVO;
import com.quanxiaoha.weblog.admin.service.AdminArticleVersionService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/article/version")
public class AdminArticleVersionController {

    @Autowired
    private AdminArticleVersionService adminArticleVersionService;

    @PostMapping("/list")
    @ApiOperationLog(description = "获取文章版本列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response listVersions(@RequestBody ArticleVersionListReqVO reqVO) {
        return adminArticleVersionService.listVersions(reqVO.getArticleId());
    }

    @PostMapping("/detail")
    @ApiOperationLog(description = "获取版本详情")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response getVersionDetail(@RequestBody ArticleVersionDetailReqVO reqVO) {
        return adminArticleVersionService.getVersionDetail(reqVO.getVersionId());
    }

    @PostMapping("/diff")
    @ApiOperationLog(description = "版本对比")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response diffVersions(@RequestBody ArticleVersionDiffReqVO reqVO) {
        return adminArticleVersionService.diffVersions(reqVO.getVersionIdA(), reqVO.getVersionIdB());
    }

    @PostMapping("/rollback")
    @ApiOperationLog(description = "版本回滚")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response rollbackVersion(@RequestBody ArticleVersionRollbackReqVO reqVO) {
        return adminArticleVersionService.rollbackVersion(reqVO.getVersionId());
    }
}