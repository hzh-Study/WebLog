package com.quanxiaoha.weblog.admin.service;

import java.util.List;

public interface ArticleTaxonomyValidateService {

    void validateArticleTaxonomy(Long categoryId, List<Long> tagIds);
}
