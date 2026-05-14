package com.quanxiaoha.weblog.admin.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecommendConfigReqVO {
    @NotNull(message = "配置 ID 不能为空")
    private Long id;
    @NotBlank(message = "配置值不能为空")
    private String configValue;
}
