package com.quanxiaoha.weblog.admin.model.vo.dashboard;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRankRspVO {
    private List<ArticleRankItemVO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArticleRankItemVO {
        private Long articleId;
        private String title;
        private Long pv;
        private Double percentage;
    }
}