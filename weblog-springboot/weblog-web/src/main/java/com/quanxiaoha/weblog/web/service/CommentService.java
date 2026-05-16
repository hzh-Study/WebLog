package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.QueryCommentListReqVO;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {

    PageResponse getCommentList(QueryCommentListReqVO reqVO);

    Response publishComment(PublishCommentReqVO reqVO, HttpServletRequest request);
}