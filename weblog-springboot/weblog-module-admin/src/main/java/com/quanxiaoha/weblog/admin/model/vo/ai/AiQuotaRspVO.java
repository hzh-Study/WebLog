package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiQuotaRspVO {
    private Integer dailyArticleGenLimit;
    private Integer dailyArticleGenLimitBase;
    private Integer dailyArticleGenUsed;
    private Integer dailyArticleGenRemaining;
    private Integer dailyArticleGen弹性额度;

    private Long dailyTokenLimit;
    private Long dailyTokenUsed;
    private Long dailyTokenRemaining;
    private Long dailyToken弹性剩余;

    private Integer standard写作次数;
    private Boolean need弹性分配;

    private Long lastInteractionTime;
    private Integer interactionIntervalSeconds;
    private Boolean canInteractNow;
    private Long remainingCooldownSeconds;

    private Boolean token预警触发;
    private Integer token预警阈值Percent;
}
