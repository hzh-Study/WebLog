package com.quanxiaoha.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleAiSummarizeRspVO {
    private String summary;
    private String keyPoints;
    private String readingTime;
    private String difficulty;
}