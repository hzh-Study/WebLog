package com.quanxiaoha.weblog.admin.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCommentReqVO {
    @NotNull(message = "评论ID不能为空")
    private Long commentId;
}