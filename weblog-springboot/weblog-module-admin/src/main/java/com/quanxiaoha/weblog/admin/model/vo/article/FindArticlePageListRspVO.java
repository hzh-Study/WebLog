package com.quanxiaoha.weblog.admin.model.vo.article;

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
public class FindArticlePageListRspVO {
    private Long id;
    private String title;
    private String titleImage;
    private String description;
    private String visibility;
    private Date createTime;
    private Date updateTime;
    private Long readNum;
    private Long likeNum;
    private Long favoriteNum;
    private String categoryName;
    private List<String> tagNames;
}
