package com.quanxiaoha.weblog.admin.model.vo.article;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionDetailRspVO {
    private Long versionId;
    private Integer versionNum;
    private String title;
    private String content;
    private String description;
    private String changeSummary;
    private String createTime;
}