package com.quanxiaoha.weblog.admin.model.vo.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxonomyTagItemRspVO {
    private Long id;
    private String name;
    private String code;
    private Long categoryId;
    private String categoryName;
    private String aliasJson;
    private Integer sort;
    private Boolean isSystem;
    private Integer status;
}
