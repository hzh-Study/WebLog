package com.quanxiaoha.weblog.web.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleContentDO;

import java.util.List;

public interface ArticleContentDao {
    ArticleContentDO selectArticleContentByArticleId(Long articleId);

    List<ArticleContentDO> selectArticleContentByArticleIds(List<Long> articleIds);
}
