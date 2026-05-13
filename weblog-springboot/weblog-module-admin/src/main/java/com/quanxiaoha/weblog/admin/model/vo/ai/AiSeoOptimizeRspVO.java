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
public class AiSeoOptimizeRspVO {
    private String optimizedTitle;
    private String optimizedDescription;
    private List<String> keywords;
    private Integer titleScore;
    private String titleSuggestion;
    private Long estimatedTokens;
    private Long actualTokens;
}