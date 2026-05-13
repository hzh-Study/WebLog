package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeReqVO;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeRspVO;

public interface ArticleAiService {

    Response<ArticleAiSummarizeRspVO> summarizeArticle(ArticleAiSummarizeReqVO reqVO);
}