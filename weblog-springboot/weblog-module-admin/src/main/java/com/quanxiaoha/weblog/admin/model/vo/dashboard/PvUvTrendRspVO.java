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
public class PvUvTrendRspVO {
    private List<String> dates;
    private List<Long> pvData;
    private List<Long> uvData;
}