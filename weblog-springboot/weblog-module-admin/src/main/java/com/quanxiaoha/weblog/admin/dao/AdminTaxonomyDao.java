package com.quanxiaoha.weblog.admin.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;

import java.util.List;

public interface AdminTaxonomyDao {
    List<CategoryDO> queryAllCategories(Long userId);

    List<CategoryDO> queryEnabledCategories(Long userId);

    CategoryDO queryCategoryById(Long categoryId);

    List<TagDO> queryTagsByCategoryId(Long categoryId, Long userId);

    List<TagDO> searchTaxonomyTags(String key, Long categoryId, Long userId);

    List<TagDO> queryTagsByIds(List<Long> tagIds);

    Page<TagDO> queryTagsByCategoryIdPaged(Long categoryId, Long userId, Long current, Long size);

    List<CategoryDO> searchTaxonomyCategories(String key, Long userId);
}
