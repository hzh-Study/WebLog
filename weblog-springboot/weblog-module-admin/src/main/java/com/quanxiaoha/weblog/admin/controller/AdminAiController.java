package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.model.vo.ai.*;
import com.quanxiaoha.weblog.admin.service.AdminAiService;
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
@RequestMapping("/admin/ai")
public class AdminAiController {

    @Autowired
    private AdminAiService aiService;

    @PostMapping("/tag/recommend")
    @ApiOperationLog(description = "AI标签推荐")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiRecommendTagRspVO> recommendTags(@RequestBody AiRecommendTagReqVO reqVO) {
        return aiService.recommendTags(reqVO);
    }

    @PostMapping("/category/recommend")
    @ApiOperationLog(description = "AI分类推荐")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiRecommendCategoryRspVO> recommendCategories(@RequestBody AiRecommendCategoryReqVO reqVO) {
        return aiService.recommendCategories(reqVO);
    }

    @PostMapping("/article/generate")
    @ApiOperationLog(description = "AI文章生成")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiGenerateArticleRspVO> generateArticle(@RequestBody @Validated AiGenerateArticleReqVO reqVO) {
        return aiService.generateArticle(reqVO);
    }

    @PostMapping("/quota")
    @ApiOperationLog(description = "查询AI使用额度")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiQuotaRspVO> getQuota() {
        return aiService.getQuota();
    }

    @PostMapping("/token/estimate")
    @ApiOperationLog(description = "预估Token消耗")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiEstimateTokenRspVO> estimateTokens(@RequestBody @Validated AiEstimateTokenReqVO reqVO) {
        return aiService.estimateTokens(reqVO);
    }

    @PostMapping("/article/continue")
    @ApiOperationLog(description = "AI文章续写")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiEditRspVO> continueArticle(@RequestBody AiContinueReqVO reqVO) {
        return aiService.continueArticle(reqVO);
    }

    @PostMapping("/article/rewrite")
    @ApiOperationLog(description = "AI文章改写")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiEditRspVO> rewriteText(@RequestBody AiRewriteReqVO reqVO) {
        return aiService.rewriteText(reqVO);
    }

    @PostMapping("/article/polish")
    @ApiOperationLog(description = "AI文章润色")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiEditRspVO> polishText(@RequestBody AiPolishReqVO reqVO) {
        return aiService.polishText(reqVO);
    }

    @PostMapping("/seo/optimize")
    @ApiOperationLog(description = "AI SEO优化")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response<AiSeoOptimizeRspVO> seoOptimize(@RequestBody @Validated AiSeoOptimizeReqVO reqVO) {
        return aiService.seoOptimize(reqVO);
    }
}
