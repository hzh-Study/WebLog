package com.quanxiaoha.weblog.admin.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCountDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;

import java.util.Date;
import java.util.List;

public interface AdminArticleDao {
    int insertArticle(ArticleDO articleDO);

    ArticleDO queryByArticleId(Long articleId, Long userId);

    Page<ArticleDO> queryArticlePageList(Long current, Long size, Date startDate, Date endDate, String searchTitle, Long userId);

    int deleteById(Long articleId);

    int updateById(ArticleDO articleDO);

    Long selectTotalCount();

    Long selectTotalCountByUserId(Long userId);

    List<ArticleCountDO> selectArticleCount(String startDate, String endDate);

    List<ArticleCountDO> selectArticleCountByUserId(Long userId, String startDate, String endDate);

    Long sumReadNumByUserId(Long userId);

    int readNumIncrease(Long articleId);

    int likeNumIncrease(Long articleId);

    int likeNumDecrease(Long articleId);

    int favoriteNumIncrease(Long articleId);

    int favoriteNumDecrease(Long articleId);

    Long selectUserIdByArticleId(Long articleId);
}
