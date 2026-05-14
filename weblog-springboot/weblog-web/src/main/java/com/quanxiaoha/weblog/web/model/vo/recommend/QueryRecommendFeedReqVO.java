package com.quanxiaoha.weblog.web.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRecommendFeedReqVO {
    @Builder.Default
    private Long current = 1L;
    @Builder.Default
    private Long size = 10L;
    private String strategy;
}
