package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCommentListReqVO {
    private Long articleId;
    private Long current = 1L;
    private Long size = 10L;
}