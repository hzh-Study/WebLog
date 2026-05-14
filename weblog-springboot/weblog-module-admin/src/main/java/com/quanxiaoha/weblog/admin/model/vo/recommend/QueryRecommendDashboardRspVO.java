package com.quanxiaoha.weblog.admin.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRecommendDashboardRspVO {
    private Long totalExposures;
    private Long totalClicks;
    private Double avgCtr;
    private Long totalFeedbacks;
    private Long activeUserCount;
}
