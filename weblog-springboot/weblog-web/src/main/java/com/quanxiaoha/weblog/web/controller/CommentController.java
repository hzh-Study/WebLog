package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.QueryCommentListReqVO;
import com.quanxiaoha.weblog.web.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/list")
    @ApiOperationLog(description = "获取评论列表")
    public Response getCommentList(@RequestBody QueryCommentListReqVO reqVO) {
        return commentService.getCommentList(reqVO);
    }

    @PostMapping("/publish")
    @ApiOperationLog(description = "发表评论")
    public Response publishComment(@RequestBody PublishCommentReqVO reqVO, HttpServletRequest request) {
        return commentService.publishComment(reqVO, request);
    }
}