package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.QueryCommentPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.ReviewCommentReqVO;
import com.quanxiaoha.weblog.admin.service.AdminCommentService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private AdminCommentService commentService;

    @PostMapping("/list")
    @ApiOperationLog(description = "获取评论分页列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response queryCommentPageList(@RequestBody QueryCommentPageListReqVO reqVO) {
        return commentService.queryCommentPageList(reqVO);
    }

    @PostMapping("/review")
    @ApiOperationLog(description = "审核评论")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response reviewComment(@RequestBody @Validated ReviewCommentReqVO reqVO) {
        return commentService.reviewComment(reqVO);
    }

    @PostMapping("/delete")
    @ApiOperationLog(description = "删除评论")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO reqVO) {
        return commentService.deleteComment(reqVO);
    }
}