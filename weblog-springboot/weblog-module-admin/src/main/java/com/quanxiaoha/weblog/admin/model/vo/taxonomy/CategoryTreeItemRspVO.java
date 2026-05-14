package com.quanxiaoha.weblog.admin.model.vo.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeItemRspVO {
    private Long id;
    private String name;
    private String code;
    private Integer level;
    private Integer sort;
    private Boolean isSystem;
    private Integer status;
    private List<CategoryTreeItemRspVO> children;
}
