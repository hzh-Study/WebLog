package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.PublicCatalogItemStatsDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.web.model.vo.tag.QueryTagListItemRspVO;
import com.quanxiaoha.weblog.web.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 前台：全站「公开、未删文章」维度下的标签；首页侧栏为 TOP10。
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements TagService {

    private static final int FRONT_CATALOG_TOP_N = 10;

    @Override
    public Response queryTagList() {
        List<QueryTagListItemRspVO> list = mapToVo(getBaseMapper().selectTopByPublicArticleCount(FRONT_CATALOG_TOP_N));
        return Response.success(list);
    }

    @Override
    public Response queryTagListAll() {
        List<QueryTagListItemRspVO> list = mapToVo(getBaseMapper().selectAllByPublicArticleCount());
        return Response.success(list);
    }

    private List<QueryTagListItemRspVO> mapToVo(List<PublicCatalogItemStatsDO> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        return rows.stream()
                .map(p -> QueryTagListItemRspVO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .articleCount(p.getArticleCount())
                        .build())
                .collect(Collectors.toList());
    }
}
