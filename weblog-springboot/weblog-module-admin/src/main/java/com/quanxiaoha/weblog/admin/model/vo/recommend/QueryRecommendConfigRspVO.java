package com.quanxiaoha.weblog.admin.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRecommendConfigRspVO {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
}
