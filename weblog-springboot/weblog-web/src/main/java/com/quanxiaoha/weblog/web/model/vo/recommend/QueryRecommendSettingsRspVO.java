package com.quanxiaoha.weblog.web.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRecommendSettingsRspVO {
    private Boolean personalizedEnabled;
    private List<String> interestTags;
}
