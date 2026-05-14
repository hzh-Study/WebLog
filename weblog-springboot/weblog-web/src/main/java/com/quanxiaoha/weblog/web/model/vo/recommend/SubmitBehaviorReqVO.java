package com.quanxiaoha.weblog.web.model.vo.recommend;


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
public class SubmitBehaviorReqVO {
    @NotBlank(message = "事件类型不能为空")
    private String eventType;
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;
    @NotBlank(message = "目标类型不能为空")
    private String targetType;
    private String eventData;
    private Long duration;
    private String referrer;
}
