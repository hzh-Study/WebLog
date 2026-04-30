package com.quanxiaoha.weblog.common.domain.dos;

import lombok.Data;

/**
 * 前台用：某分类/标签在「公开未删文章」中的篇数，用于全站统计与排行。
 */
@Data
public class PublicCatalogItemStatsDO {
    private Long id;
    private String name;
    private Long articleCount;
}
