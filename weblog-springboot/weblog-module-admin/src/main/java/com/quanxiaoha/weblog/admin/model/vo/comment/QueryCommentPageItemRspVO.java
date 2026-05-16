package com.quanxiaoha.weblog.admin.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCommentPageItemRspVO {
    private Long id;
    private Long articleId;
    private String articleTitle;
    private Long parentId;
    private Long rootId;
    private Long replyToCommentId;
    private String nickname;
    private String content;
    private Integer status;
    private Date createTime;
}