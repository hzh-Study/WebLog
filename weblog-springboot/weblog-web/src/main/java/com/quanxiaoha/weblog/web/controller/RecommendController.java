package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryHotArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRecommendFeedReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRelatedArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitFeedbackReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.UpdateRecommendSettingsReqVO;
import com.quanxiaoha.weblog.web.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @PostMapping("/feed")
    @ApiOperationLog(description = "获取推荐流")
    public PageResponse queryRecommendFeed(@RequestBody @Validated QueryRecommendFeedReqVO reqVO) {
        return recommendService.queryRecommendFeed(reqVO);
    }

    @PostMapping("/related")
    @ApiOperationLog(description = "获取关联推荐文章")
    public Response queryRelatedArticles(@RequestBody @Validated QueryRelatedArticleReqVO reqVO) {
        return recommendService.queryRelatedArticles(reqVO);
    }

    @PostMapping("/hot")
    @ApiOperationLog(description = "获取热门推荐文章")
    public Response queryHotArticles(@RequestBody @Validated QueryHotArticleReqVO reqVO) {
        return recommendService.queryHotArticles(reqVO);
    }

    @PostMapping("/behavior")
    @ApiOperationLog(description = "提交用户行为日志")
    public Response submitBehavior(@RequestBody @Validated SubmitBehaviorReqVO reqVO) {
        return recommendService.submitBehavior(reqVO);
    }

    @PostMapping("/feedback")
    @ApiOperationLog(description = "提交推荐反馈")
    public Response submitFeedback(@RequestBody @Validated SubmitFeedbackReqVO reqVO) {
        return recommendService.submitFeedback(reqVO);
    }

    @PostMapping("/settings")
    @ApiOperationLog(description = "查询推荐偏好设置")
    public Response queryRecommendSettings() {
        return recommendService.queryRecommendSettings();
    }

    @PostMapping("/settings/update")
    @ApiOperationLog(description = "更新推荐偏好设置")
    public Response updateRecommendSettings(@RequestBody @Validated UpdateRecommendSettingsReqVO reqVO) {
        return recommendService.updateRecommendSettings(reqVO);
    }
}
