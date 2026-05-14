package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.taxonomy.QueryTagByCategoryReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.SearchTaxonomyCategoryReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.SearchTaxonomyTagReqVO;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.ValidateTagReqVO;
import com.quanxiaoha.weblog.common.Response;

public interface AdminTaxonomyService {
    Response queryCategoryTree();

    Response queryTagsByCategory(QueryTagByCategoryReqVO reqVO);

    Response searchTaxonomyTags(SearchTaxonomyTagReqVO reqVO);

    Response searchTaxonomyCategories(SearchTaxonomyCategoryReqVO reqVO);

    Response validateTags(ValidateTagReqVO reqVO);
}
