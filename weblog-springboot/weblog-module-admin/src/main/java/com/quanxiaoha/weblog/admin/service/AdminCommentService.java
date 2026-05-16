package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.QueryCommentPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.ReviewCommentReqVO;
import com.quanxiaoha.weblog.common.Response;

public interface AdminCommentService {

    Response queryCommentPageList(QueryCommentPageListReqVO reqVO);

    Response reviewComment(ReviewCommentReqVO reqVO);

    Response deleteComment(DeleteCommentReqVO reqVO);
}