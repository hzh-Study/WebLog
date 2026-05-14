package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleCategoryRelDO;

import java.util.List;

public interface AdminArticleCategoryRelDao {
    int insert(ArticleCategoryRelDO articleCategoryRelDO);

    ArticleCategoryRelDO selectByArticleId(Long articleId);

    List<ArticleCategoryRelDO> selectByArticleIds(List<Long> articleIds);

    List<ArticleCategoryRelDO> selectByCategoryId(Long categoryId);

    int deleteByArticleId(Long articleId);
}
