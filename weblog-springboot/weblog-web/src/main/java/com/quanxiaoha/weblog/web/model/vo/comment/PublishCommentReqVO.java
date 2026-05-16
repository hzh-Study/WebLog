package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishCommentReqVO {
    private Long articleId;
    private Long parentId;
    private Long replyToCommentId;
    private String nickname;
    private String email;
    private String website;
    private String content;
}