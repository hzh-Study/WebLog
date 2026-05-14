package com.quanxiaoha.weblog.admin.model.vo.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchTaxonomyCategoryReqVO {
    private String key;
}
