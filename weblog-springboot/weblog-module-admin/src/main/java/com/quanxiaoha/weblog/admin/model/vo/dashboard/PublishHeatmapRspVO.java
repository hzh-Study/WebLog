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
public class PublishHeatmapRspVO {
    private List<PublishHeatmapItemVO> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublishHeatmapItemVO {
        private String date;
        private Integer count;
    }
}