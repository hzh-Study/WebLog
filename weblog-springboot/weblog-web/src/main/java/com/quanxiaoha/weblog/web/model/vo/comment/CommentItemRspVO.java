package com.quanxiaoha.weblog.web.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentItemRspVO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private Long rootId;
    private Long replyToCommentId;
    private String nickname;
    private String content;
    private String website;
    private String createTime;
    private String replyToNickname;
    private List<CommentItemRspVO> children;
}