package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleLikeDO;

public interface AdminArticleLikeDao {

    void insert(ArticleLikeDO record);

    void deleteByArticleIdAndUserId(Long articleId, Long userId);

    void deleteByArticleIdAndVisitorId(Long articleId, String visitorId);

    ArticleLikeDO selectByArticleIdAndUserId(Long articleId, Long userId);

    ArticleLikeDO selectByArticleIdAndVisitorId(Long articleId, String visitorId);

    Long selectCountByArticleId(Long articleId);
}