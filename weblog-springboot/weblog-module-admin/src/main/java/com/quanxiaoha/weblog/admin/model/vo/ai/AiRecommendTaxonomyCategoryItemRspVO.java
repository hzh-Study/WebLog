package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiRecommendTaxonomyCategoryItemRspVO {
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private Long parentCategoryId;
    private String parentCategoryName;
    private Double confidence;
    private String reason;
}
