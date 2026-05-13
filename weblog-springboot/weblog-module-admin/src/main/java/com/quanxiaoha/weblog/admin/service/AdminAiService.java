package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.ai.*;
import com.quanxiaoha.weblog.common.Response;

public interface AdminAiService {

    Response<AiRecommendTagRspVO> recommendTags(AiRecommendTagReqVO reqVO);

    Response<AiRecommendCategoryRspVO> recommendCategories(AiRecommendCategoryReqVO reqVO);

    Response<AiGenerateArticleRspVO> generateArticle(AiGenerateArticleReqVO reqVO);

    Response<AiQuotaRspVO> getQuota();

    Response<AiEstimateTokenRspVO> estimateTokens(AiEstimateTokenReqVO reqVO);

    Response<AiEditRspVO> continueArticle(AiContinueReqVO reqVO);

    Response<AiEditRspVO> rewriteText(AiRewriteReqVO reqVO);

    Response<AiEditRspVO> polishText(AiPolishReqVO reqVO);

    Response<AiSeoOptimizeRspVO> seoOptimize(AiSeoOptimizeReqVO reqVO);
}
