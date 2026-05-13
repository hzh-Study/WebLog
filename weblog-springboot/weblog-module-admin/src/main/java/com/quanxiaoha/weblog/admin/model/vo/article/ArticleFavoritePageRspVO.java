package com.quanxiaoha.weblog.admin.model.vo.article;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleFavoritePageRspVO {
    private List<FavoriteItemVO> records;
    private Long current;
    private Long size;
    private Long total;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FavoriteItemVO {
        private Long articleId;
        private String title;
        private String description;
        private String titleImage;
        private String createTime;
        private Long readNum;
        private Long likeNum;
    }
}