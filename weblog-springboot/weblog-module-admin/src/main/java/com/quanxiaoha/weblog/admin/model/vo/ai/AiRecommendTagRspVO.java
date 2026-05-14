package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiRecommendTagRspVO {
    private List<String> tags;
    private List<AiRecommendTaxonomyTagItemRspVO> suggestions;
    private List<String> unmatchedTerms;
    private Long estimatedTokens;
    private Long actualTokens;
}
