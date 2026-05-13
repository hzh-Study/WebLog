package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleFavoriteDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleLikeDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoritePageRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoriteRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleLikeRspVO;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleFavoriteDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleLikeDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("adminArticleInteractionService")
@Slf4j
public class AdminArticleInteractionServiceImpl {

    @Autowired
    private AdminArticleLikeDao articleLikeDao;
    @Autowired
    private AdminArticleFavoriteDao articleFavoriteDao;
    @Autowired
    private AdminArticleDao articleDao;
    @Autowired
    private AdminUserDao userDao;
    @Autowired
    private ArticleMapper articleMapper;

    public ArticleLikeRspVO toggleLike(Long articleId, Long userId, String visitorId) {
        ArticleDO articleDO = getPublicArticle(articleId);

        if (userId != null) {
            ArticleLikeDO existing = articleLikeDao.selectByArticleIdAndUserId(articleId, userId);
            if (existing != null) {
                articleLikeDao.deleteByArticleIdAndUserId(articleId, userId);
                decrementLikeNum(articleDO);
                return ArticleLikeRspVO.builder().liked(false).likeCount(articleLikeDao.selectCountByArticleId(articleId)).build();
            } else {
                articleLikeDao.insert(ArticleLikeDO.builder()
                        .articleId(articleId)
                        .userId(userId)
                        .createTime(new Date())
                        .build());
                incrementLikeNum(articleDO);
                return ArticleLikeRspVO.builder().liked(true).likeCount(articleLikeDao.selectCountByArticleId(articleId)).build();
            }
        } else {
            ArticleLikeDO existing = articleLikeDao.selectByArticleIdAndVisitorId(articleId, visitorId);
            if (existing != null) {
                articleLikeDao.deleteByArticleIdAndVisitorId(articleId, visitorId);
                decrementLikeNum(articleDO);
                return ArticleLikeRspVO.builder().liked(false).likeCount(articleLikeDao.selectCountByArticleId(articleId)).build();
            } else {
                articleLikeDao.insert(ArticleLikeDO.builder()
                        .articleId(articleId)
                        .visitorId(visitorId)
                        .createTime(new Date())
                        .build());
                incrementLikeNum(articleDO);
                return ArticleLikeRspVO.builder().liked(true).likeCount(articleLikeDao.selectCountByArticleId(articleId)).build();
            }
        }
    }

    public ArticleLikeRspVO getLikeStatus(Long articleId, Long userId, String visitorId) {
        getPublicArticle(articleId);
        Long likeCount = articleLikeDao.selectCountByArticleId(articleId);
        boolean liked;
        if (userId != null) {
            liked = articleLikeDao.selectByArticleIdAndUserId(articleId, userId) != null;
        } else {
            liked = articleLikeDao.selectByArticleIdAndVisitorId(articleId, visitorId) != null;
        }
        return ArticleLikeRspVO.builder().liked(liked).likeCount(likeCount).build();
    }

    public ArticleFavoriteRspVO toggleFavorite(Long articleId, Long userId) {
        if (userId == null) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        ArticleDO articleDO = getPublicArticle(articleId);
        ArticleFavoriteDO existing = articleFavoriteDao.selectByArticleIdAndUserId(articleId, userId);
        if (existing != null) {
            articleFavoriteDao.deleteByArticleIdAndUserId(articleId, userId);
            decrementFavoriteNum(articleDO);
            return ArticleFavoriteRspVO.builder().favorited(false).favoriteCount(articleFavoriteDao.selectCountByArticleId(articleId)).build();
        } else {
            articleFavoriteDao.insert(ArticleFavoriteDO.builder()
                    .articleId(articleId)
                    .userId(userId)
                    .createTime(new Date())
                    .build());
            incrementFavoriteNum(articleDO);
            return ArticleFavoriteRspVO.builder().favorited(true).favoriteCount(articleFavoriteDao.selectCountByArticleId(articleId)).build();
        }
    }

    public ArticleFavoritePageRspVO getFavoriteList(Long userId, Long current, Long size) {
        List<ArticleFavoriteDO> favorites = articleFavoriteDao.selectPageByUserId(userId, current, size);
        List<ArticleFavoritePageRspVO.FavoriteItemVO> records = Collections.emptyList();
        long total = 0L;
        if (!CollectionUtils.isEmpty(favorites)) {
            List<Long> articleIds = favorites.stream().map(ArticleFavoriteDO::getArticleId).collect(Collectors.toList());
            List<ArticleDO> articles = articleMapper.selectBatchIds(articleIds);
            Map<Long, ArticleDO> articleMap = articles.stream().collect(Collectors.toMap(ArticleDO::getId, a -> a, (a, b) -> a));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            records = favorites.stream()
                    .map(fav -> {
                        ArticleDO article = articleMap.get(fav.getArticleId());
                        if (article == null) {
                            return null;
                        }
                        return ArticleFavoritePageRspVO.FavoriteItemVO.builder()
                                .articleId(fav.getArticleId())
                                .title(article.getTitle())
                                .description(article.getDescription())
                                .titleImage(article.getTitleImage())
                                .createTime(article.getCreateTime() == null ? null : sdf.format(article.getCreateTime()))
                                .readNum(article.getReadNum())
                                .likeNum(article.getLikeNum())
                                .build();
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            total = records.size();
        }
        return ArticleFavoritePageRspVO.builder()
                .records(records)
                .current(current)
                .size(size)
                .total(total)
                .build();
    }

    public Long resolveUserId(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        UserDO userDO = userDao.selectByUsername(username);
        return userDO != null ? userDO.getId() : null;
    }

    private void incrementLikeNum(ArticleDO articleDO) {
        articleDao.likeNumIncrease(articleDO.getId());
    }

    private void decrementLikeNum(ArticleDO articleDO) {
        articleDao.likeNumDecrease(articleDO.getId());
    }

    private void incrementFavoriteNum(ArticleDO articleDO) {
        articleDao.favoriteNumIncrease(articleDO.getId());
    }

    private void decrementFavoriteNum(ArticleDO articleDO) {
        articleDao.favoriteNumDecrease(articleDO.getId());
    }

    private ArticleDO getPublicArticle(Long articleId) {
        ArticleDO articleDO = articleMapper.selectOne(new LambdaQueryWrapper<ArticleDO>()
                .eq(ArticleDO::getId, articleId)
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC));
        if (articleDO == null) {
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        return articleDO;
    }
}