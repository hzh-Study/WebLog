package com.quanxiaoha.weblog.web.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryHotArticleReqVO {
    @Builder.Default
    private String period = "week";
    @Builder.Default
    private Integer limit = 10;
}
