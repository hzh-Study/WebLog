package com.quanxiaoha.weblog.web.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRecommendFeedRspVO {
    private Long id;
    private String title;
    private String description;
    private String titleImage;
    private String categoryName;
    private List<String> tags;
    private String author;
    private Date createTime;
    private Long readNum;
    private Double hotScore;
}
