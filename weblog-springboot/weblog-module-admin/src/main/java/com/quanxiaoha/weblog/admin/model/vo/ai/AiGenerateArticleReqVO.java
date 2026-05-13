package com.quanxiaoha.weblog.admin.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiGenerateArticleReqVO {
    @NotBlank(message = "写作要求不能为空")
    private String prompt;
    private String title;
    private String categoryName;
    private List<String> tags;
}
