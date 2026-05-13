package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.ArticleVersionDO;

import java.util.List;

public interface AdminArticleVersionDao {

    void insert(ArticleVersionDO version);

    Integer selectMaxVersionNum(Long articleId);

    List<ArticleVersionDO> selectListByArticleId(Long articleId);

    ArticleVersionDO selectById(Long id);

    void deleteOldestExceed(Long articleId, Integer keepCount);
}