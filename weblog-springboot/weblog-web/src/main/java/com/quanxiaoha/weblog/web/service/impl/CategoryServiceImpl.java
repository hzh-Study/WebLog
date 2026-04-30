package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.PublicCatalogItemStatsDO;
import com.quanxiaoha.weblog.common.domain.mapper.CategoryMapper;
import com.quanxiaoha.weblog.web.model.vo.category.QueryCategoryListItemRspVO;
import com.quanxiaoha.weblog.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 前台：全站「公开、未删文章」维度下的分类列表；首页侧栏为 TOP10。
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryDO> implements CategoryService {

    private static final int FRONT_CATALOG_TOP_N = 10;

    @Override
    public Response queryCategoryList() {
        List<QueryCategoryListItemRspVO> list = mapToVo(getBaseMapper().selectTopByPublicArticleCount(FRONT_CATALOG_TOP_N));
        return Response.success(list);
    }

    @Override
    public Response queryCategoryListAll() {
        List<QueryCategoryListItemRspVO> list = mapToVo(getBaseMapper().selectAllByPublicArticleCount());
        return Response.success(list);
    }

    private List<QueryCategoryListItemRspVO> mapToVo(List<PublicCatalogItemStatsDO> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        return rows.stream()
                .map(p -> QueryCategoryListItemRspVO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .articleCount(p.getArticleCount())
                        .build())
                .collect(Collectors.toList());
    }
}
