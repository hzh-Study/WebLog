package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleFavoriteDO;

import java.util.List;

public interface AdminArticleFavoriteDao {

    void insert(ArticleFavoriteDO record);

    void deleteByArticleIdAndUserId(Long articleId, Long userId);

    ArticleFavoriteDO selectByArticleIdAndUserId(Long articleId, Long userId);

    List<ArticleFavoriteDO> selectPageByUserId(Long userId, Long current, Long size);

    Long selectCountByUserId(Long userId);

    Long selectCountByArticleId(Long articleId);
}