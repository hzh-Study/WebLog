package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryHotArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRecommendFeedReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.QueryRelatedArticleReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitBehaviorReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.SubmitFeedbackReqVO;
import com.quanxiaoha.weblog.web.model.vo.recommend.UpdateRecommendSettingsReqVO;

public interface RecommendService {

    PageResponse queryRecommendFeed(QueryRecommendFeedReqVO queryRecommendFeedReqVO);

    Response queryRelatedArticles(QueryRelatedArticleReqVO queryRelatedArticleReqVO);

    Response queryHotArticles(QueryHotArticleReqVO queryHotArticleReqVO);

    Response submitBehavior(SubmitBehaviorReqVO submitBehaviorReqVO);

    Response submitFeedback(SubmitFeedbackReqVO submitFeedbackReqVO);

    Response queryRecommendSettings();

    Response updateRecommendSettings(UpdateRecommendSettingsReqVO updateRecommendSettingsReqVO);
}
