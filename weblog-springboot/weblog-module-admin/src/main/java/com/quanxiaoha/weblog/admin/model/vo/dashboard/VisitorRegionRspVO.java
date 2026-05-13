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
public class VisitorRegionRspVO {
    private List<RegionItemVO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegionItemVO {
        private String region;
        private Long count;
    }
}