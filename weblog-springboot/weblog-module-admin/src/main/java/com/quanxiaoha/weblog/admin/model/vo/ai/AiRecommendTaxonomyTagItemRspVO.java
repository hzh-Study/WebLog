package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiRecommendTaxonomyTagItemRspVO {
    private Long tagId;
    private String tagName;
    private String tagCode;
    private Long categoryId;
    private String categoryName;
    private Double confidence;
    private String reason;
}
