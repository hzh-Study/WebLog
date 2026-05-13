package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeReqVO;
import com.quanxiaoha.weblog.web.model.vo.article.ArticleAiSummarizeRspVO;
import com.quanxiaoha.weblog.web.service.ArticleAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article/ai")
public class ArticleAiController {

    @Autowired
    private ArticleAiService articleAiService;

    @PostMapping("/summarize")
    @ApiOperationLog(description = "AI文章摘要")
    public Response<ArticleAiSummarizeRspVO> summarizeArticle(@RequestBody @Validated ArticleAiSummarizeReqVO reqVO) {
        return articleAiService.summarizeArticle(reqVO);
    }
}