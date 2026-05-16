package com.quanxiaoha.weblog.admin.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCommentPageListReqVO {
    private Long current = 1L;
    private Long size = 10L;
    private Integer status;
    private String nickname;
    private String articleTitle;
}