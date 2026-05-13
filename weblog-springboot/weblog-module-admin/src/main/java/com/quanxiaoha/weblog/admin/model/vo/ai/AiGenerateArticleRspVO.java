package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiGenerateArticleRspVO {
    private String title;
    private String content;
    private Long estimatedTokens;
    private Long actualTokens;
}
