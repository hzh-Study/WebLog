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
public class SubmitFeedbackReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;
    @NotBlank(message = "反馈类型不能为空")
    private String feedbackType;
    private String scene;
}
