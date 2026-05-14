package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.service.ArticleTaxonomyValidateService;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleTaxonomyValidateServiceImpl implements ArticleTaxonomyValidateService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public void validateArticleTaxonomy(Long categoryId, List<Long> tagIds) {
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null || Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new BizException(ResponseCodeEnum.TAXONOMY_CATEGORY_NOT_FOUND);
        }
        if (category.getStatus() != null && category.getStatus() != 1) {
            throw new BizException(ResponseCodeEnum.TAXONOMY_CATEGORY_DISABLED);
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                TagDO tag = tagMapper.selectById(tagId);
                if (tag == null || Boolean.TRUE.equals(tag.getIsDeleted())) {
                    throw new BizException(ResponseCodeEnum.TAXONOMY_TAG_NOT_FOUND);
                }
                if (tag.getStatus() != null && tag.getStatus() != 1) {
                    throw new BizException(ResponseCodeEnum.TAXONOMY_TAG_DISABLED);
                }
                if (tag.getCategoryId() != null && !tag.getCategoryId().equals(categoryId)) {
                    throw new BizException(ResponseCodeEnum.TAXONOMY_TAG_NOT_IN_CATEGORY);
                }
            }
        }
    }
}
