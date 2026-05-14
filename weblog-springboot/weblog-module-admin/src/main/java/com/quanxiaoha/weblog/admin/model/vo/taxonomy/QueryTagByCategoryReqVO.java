package com.quanxiaoha.weblog.admin.model.vo.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryTagByCategoryReqVO {
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @Builder.Default
    private Long current = 1L;
    @Builder.Default
    private Long size = 50L;
}
