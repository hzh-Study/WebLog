package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.dao.AdminTaxonomyDao;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AdminTaxonomyDaoImpl implements AdminTaxonomyDao {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<CategoryDO> queryAllCategories(Long userId) {
        QueryWrapper<CategoryDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(CategoryDO::getUserId, userId)
                .eq(CategoryDO::getIsDeleted, 0)
                .orderByAsc(CategoryDO::getSort)
                .orderByAsc(CategoryDO::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<CategoryDO> queryEnabledCategories(Long userId) {
        QueryWrapper<CategoryDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(CategoryDO::getUserId, userId)
                .eq(CategoryDO::getIsDeleted, 0)
                .eq(CategoryDO::getStatus, 1)
                .orderByAsc(CategoryDO::getSort)
                .orderByAsc(CategoryDO::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public CategoryDO queryCategoryById(Long categoryId) {
        QueryWrapper<CategoryDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(CategoryDO::getId, categoryId)
                .eq(CategoryDO::getIsDeleted, 0);
        return categoryMapper.selectOne(wrapper);
    }

    @Override
    public List<TagDO> queryTagsByCategoryId(Long categoryId, Long userId) {
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0)
                .eq(TagDO::getCategoryId, categoryId)
                .orderByAsc(TagDO::getSort)
                .orderByAsc(TagDO::getId);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public List<TagDO> searchTaxonomyTags(String key, Long categoryId, Long userId) {
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, false)
                .eq(TagDO::getStatus, 1)
                .eq(Objects.nonNull(categoryId), TagDO::getCategoryId, categoryId);
        if (key != null && !key.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(TagDO::getName, key)
                    .or()
                    .like(TagDO::getCode, key)
                    .or()
                    .like(TagDO::getAliasJson, key)
            );
        }
        wrapper.orderByAsc(TagDO::getName);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public List<TagDO> queryTagsByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(TagDO::getId, tagIds)
                .eq(TagDO::getIsDeleted, 0);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public Page<TagDO> queryTagsByCategoryIdPaged(Long categoryId, Long userId, Long current, Long size) {
        Page<TagDO> page = new Page<>(current, size);
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0)
                .eq(TagDO::getCategoryId, categoryId)
                .orderByAsc(TagDO::getSort)
                .orderByAsc(TagDO::getId);
        return tagMapper.selectPage(page, wrapper);
    }

    @Override
    public List<CategoryDO> searchTaxonomyCategories(String key, Long userId) {
        LambdaQueryWrapper<CategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryDO::getUserId, userId)
                .eq(CategoryDO::getIsDeleted, false)
                .eq(CategoryDO::getStatus, 1);
        if (key != null && !key.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(CategoryDO::getName, key)
                    .or()
                    .like(CategoryDO::getCode, key)
            );
        }
        wrapper.orderByAsc(CategoryDO::getSort).orderByAsc(CategoryDO::getId);
        return categoryMapper.selectList(wrapper);
    }
}
