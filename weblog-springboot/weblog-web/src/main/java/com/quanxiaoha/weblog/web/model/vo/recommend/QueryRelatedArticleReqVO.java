package com.quanxiaoha.weblog.web.model.vo.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryRelatedArticleReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;
    @Builder.Default
    private Integer limit = 6;
}
