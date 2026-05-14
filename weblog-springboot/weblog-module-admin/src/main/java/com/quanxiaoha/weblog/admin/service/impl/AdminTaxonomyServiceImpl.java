package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.dao.AdminTaxonomyDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.taxonomy.*;
import com.quanxiaoha.weblog.admin.service.AdminTaxonomyService;
import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTaxonomyServiceImpl implements AdminTaxonomyService {

    @Autowired
    private AdminTaxonomyDao taxonomyDao;

    @Autowired
    private AdminUserDao userDao;

    @Override
    public Response queryCategoryTree() {
        Long userId = getCurrentUser().getId();
        List<CategoryDO> categories = taxonomyDao.queryEnabledCategories(userId);
        List<CategoryTreeItemRspVO> tree = buildCategoryTree(categories);
        return Response.success(tree);
    }

    @Override
    public Response queryTagsByCategory(QueryTagByCategoryReqVO reqVO) {
        Long userId = getCurrentUser().getId();
        Long categoryId = reqVO.getCategoryId();
        Long current = reqVO.getCurrent();
        Long size = reqVO.getSize();

        Page<TagDO> page = taxonomyDao.queryTagsByCategoryIdPaged(categoryId, userId, current, size);

        Map<Long, String> categoryNameMap = buildCategoryNameMap(userId);

        List<TaxonomyTagItemRspVO> voList = null;
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            voList = page.getRecords().stream()
                    .map(tag -> buildTaxonomyTagItem(tag, categoryNameMap))
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, voList);
    }

    @Override
    public Response searchTaxonomyTags(SearchTaxonomyTagReqVO reqVO) {
        Long userId = getCurrentUser().getId();
        String key = reqVO.getKey();
        Long categoryId = reqVO.getCategoryId();

        List<TagDO> tags = taxonomyDao.searchTaxonomyTags(key, categoryId, userId);

        Map<Long, String> categoryNameMap = buildCategoryNameMap(userId);

        List<TaxonomyTagItemRspVO> voList = null;
        if (!CollectionUtils.isEmpty(tags)) {
            voList = tags.stream()
                    .map(tag -> buildTaxonomyTagItem(tag, categoryNameMap))
                    .collect(Collectors.toList());
        }

        return Response.success(voList);
    }

    @Override
    public Response searchTaxonomyCategories(SearchTaxonomyCategoryReqVO reqVO) {
        Long userId = getCurrentUser().getId();
        String key = reqVO.getKey();

        List<CategoryDO> categories = taxonomyDao.searchTaxonomyCategories(key, userId);

        List<CategoryTreeItemRspVO> voList = null;
        if (!CollectionUtils.isEmpty(categories)) {
            voList = categories.stream()
                    .map(c -> CategoryTreeItemRspVO.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .code(c.getCode())
                            .level(c.getLevel())
                            .sort(c.getSort())
                            .isSystem(c.getIsSystem())
                            .status(c.getStatus())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(voList);
    }

    @Override
    public Response validateTags(ValidateTagReqVO reqVO) {
        Long categoryId = reqVO.getCategoryId();
        List<Long> tagIds = reqVO.getTagIds();

        CategoryDO category = taxonomyDao.queryCategoryById(categoryId);
        if (category == null) {
            return Response.success(ValidateTagRspVO.builder()
                    .valid(false)
                    .reason("分类不存在")
                    .build());
        }
        if (category.getStatus() == null || category.getStatus() != 1) {
            return Response.success(ValidateTagRspVO.builder()
                    .valid(false)
                    .reason("分类未启用")
                    .build());
        }

        List<TagDO> tags = taxonomyDao.queryTagsByIds(tagIds);
        Map<Long, TagDO> tagMap = tags.stream()
                .collect(Collectors.toMap(TagDO::getId, t -> t, (a, b) -> a));

        List<Long> invalidTagIds = new ArrayList<>();
        List<String> reasons = new ArrayList<>();

        for (Long tagId : tagIds) {
            TagDO tag = tagMap.get(tagId);
            if (tag == null) {
                invalidTagIds.add(tagId);
                if (reasons.isEmpty()) {
                    reasons.add("标签ID " + tagId + " 不存在");
                }
            } else if (tag.getCategoryId() == null || !tag.getCategoryId().equals(categoryId)) {
                invalidTagIds.add(tagId);
                if (reasons.isEmpty()) {
                    reasons.add("标签 " + tag.getName() + " 不属于指定分类");
                }
            } else if (tag.getStatus() == null || tag.getStatus() != 1) {
                invalidTagIds.add(tagId);
                if (reasons.isEmpty()) {
                    reasons.add("标签 " + tag.getName() + " 未启用");
                }
            }
        }

        if (invalidTagIds.isEmpty()) {
            return Response.success(ValidateTagRspVO.builder()
                    .valid(true)
                    .invalidTagIds(Collections.emptyList())
                    .build());
        }

        return Response.success(ValidateTagRspVO.builder()
                .valid(false)
                .invalidTagIds(invalidTagIds)
                .reason(reasons.get(0))
                .build());
    }

    private List<CategoryTreeItemRspVO> buildCategoryTree(List<CategoryDO> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return Collections.emptyList();
        }

        Map<Long, List<CategoryDO>> parentIdMap = categories.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() != null ? c.getParentId() : 0L));

        return buildChildren(parentIdMap, 0L);
    }

    private List<CategoryTreeItemRspVO> buildChildren(Map<Long, List<CategoryDO>> parentIdMap, Long parentId) {
        List<CategoryDO> children = parentIdMap.get(parentId);
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        return children.stream()
                .map(c -> CategoryTreeItemRspVO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .code(c.getCode())
                        .level(c.getLevel())
                        .sort(c.getSort())
                        .isSystem(c.getIsSystem())
                        .status(c.getStatus())
                        .children(buildChildren(parentIdMap, c.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private Map<Long, String> buildCategoryNameMap(Long userId) {
        List<CategoryDO> allCategories = taxonomyDao.queryAllCategories(userId);
        if (CollectionUtils.isEmpty(allCategories)) {
            return Collections.emptyMap();
        }
        return allCategories.stream()
                .collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName, (a, b) -> a));
    }

    private TaxonomyTagItemRspVO buildTaxonomyTagItem(TagDO tag, Map<Long, String> categoryNameMap) {
        String categoryName = tag.getCategoryId() != null ? categoryNameMap.get(tag.getCategoryId()) : null;
        return TaxonomyTagItemRspVO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .code(tag.getCode())
                .categoryId(tag.getCategoryId())
                .categoryName(categoryName)
                .aliasJson(tag.getAliasJson())
                .sort(tag.getSort())
                .isSystem(tag.getIsSystem())
                .status(tag.getStatus())
                .build();
    }

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = userDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }
}
