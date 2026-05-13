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
public class CategoryRatioRspVO {
    private List<RatioItemVO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RatioItemVO {
        private String name;
        private Long count;
        private Double percentage;
    }
}