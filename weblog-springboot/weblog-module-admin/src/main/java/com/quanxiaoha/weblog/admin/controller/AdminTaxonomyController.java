package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.taxonomy.QueryTagByCategoryReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.SearchTaxonomyCategoryReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.SearchTaxonomyTagReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.ValidateTagReqVO;
import com.quanxiaoha.weblog.admin.service.AdminTaxonomyService;
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
@RequestMapping("/admin/taxonomy")
public class AdminTaxonomyController {

    @Autowired
    private AdminTaxonomyService taxonomyService;

    @PostMapping("/category/tree")
    @ApiOperationLog(description = "分类树查询")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response queryCategoryTree() {
        return taxonomyService.queryCategoryTree();
    }

    @PostMapping("/category/search")
    @ApiOperationLog(description = "搜索分类")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response searchTaxonomyCategories(@RequestBody @Validated SearchTaxonomyCategoryReqVO reqVO) {
        return taxonomyService.searchTaxonomyCategories(reqVO);
    }

    @PostMapping("/tag/list-by-category")
    @ApiOperationLog(description = "按分类查询标签")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response queryTagsByCategory(@RequestBody @Validated QueryTagByCategoryReqVO reqVO) {
        return taxonomyService.queryTagsByCategory(reqVO);
    }

    @PostMapping("/tag/search")
    @ApiOperationLog(description = "搜索标签")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response searchTaxonomyTags(@RequestBody @Validated SearchTaxonomyTagReqVO reqVO) {
        return taxonomyService.searchTaxonomyTags(reqVO);
    }

    @PostMapping("/tag/validate")
    @ApiOperationLog(description = "批量校验标签")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response validateTags(@RequestBody @Validated ValidateTagReqVO reqVO) {
        return taxonomyService.validateTags(reqVO);
    }
}
