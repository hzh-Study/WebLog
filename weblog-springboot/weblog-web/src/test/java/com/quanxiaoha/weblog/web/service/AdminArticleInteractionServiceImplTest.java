package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleFavoriteDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleLikeDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoritePageRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoriteRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleLikeRspVO;
import com.quanxiaoha.weblog.admin.service.impl.AdminArticleInteractionServiceImpl;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleFavoriteDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminArticleInteractionServiceImplTest {

    @Test
    void getFavoriteListShouldFilterMissingArticlesAndUseVisibleCount() {
        AdminArticleInteractionServiceImpl service = new AdminArticleInteractionServiceImpl();
        AdminArticleFavoriteDao favoriteDao = mock(AdminArticleFavoriteDao.class);
        ArticleMapper articleMapper = mock(ArticleMapper.class);

        ReflectionTestUtils.setField(service, "articleFavoriteDao", favoriteDao);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(service, "articleLikeDao", mock(AdminArticleLikeDao.class));
        ReflectionTestUtils.setField(service, "articleDao", mock(AdminArticleDao.class));
        ReflectionTestUtils.setField(service, "userDao", mock(AdminUserDao.class));

        when(favoriteDao.selectPageByUserId(7L, 1L, 10L)).thenReturn(Arrays.asList(
                ArticleFavoriteDO.builder().articleId(101L).createTime(new Date()).build(),
                ArticleFavoriteDO.builder().articleId(102L).createTime(new Date()).build()));
        when(articleMapper.selectBatchIds(Arrays.asList(101L, 102L))).thenReturn(Collections.singletonList(
                ArticleDO.builder()
                        .id(101L)
                        .title("公开文章")
                        .description("描述")
                        .titleImage("cover.png")
                        .createTime(new Date(1710000000000L))
                        .readNum(12L)
                        .likeNum(3L)
                        .build()));

        ArticleFavoritePageRspVO response = service.getFavoriteList(7L, 1L, 10L);

        Assertions.assertEquals(1L, response.getTotal());
        Assertions.assertEquals(1, response.getRecords().size());
        Assertions.assertEquals(101L, response.getRecords().get(0).getArticleId());
        Assertions.assertEquals("公开文章", response.getRecords().get(0).getTitle());
        verify(favoriteDao, never()).selectCountByUserId(any());
    }

    @Test
    void toggleFavoriteShouldAdjustFavoriteNumAtomically() {
        AdminArticleInteractionServiceImpl service = new AdminArticleInteractionServiceImpl();
        AdminArticleFavoriteDao favoriteDao = mock(AdminArticleFavoriteDao.class);
        ArticleMapper articleMapper = mock(ArticleMapper.class);
        AdminArticleDao articleDao = mock(AdminArticleDao.class);

        ReflectionTestUtils.setField(service, "articleFavoriteDao", favoriteDao);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(service, "articleDao", articleDao);
        ReflectionTestUtils.setField(service, "articleLikeDao", mock(AdminArticleLikeDao.class));
        ReflectionTestUtils.setField(service, "userDao", mock(AdminUserDao.class));

        when(articleMapper.selectOne(any())).thenReturn(ArticleDO.builder().id(9L).favoriteNum(0L).build());
        when(favoriteDao.selectByArticleIdAndUserId(9L, 5L)).thenReturn(null);
        when(favoriteDao.selectCountByArticleId(9L)).thenReturn(1L);
        doAnswer(invocation -> {
            Long articleId = invocation.getArgument(0);
            Assertions.assertEquals(9L, articleId);
            return 1;
        }).when(articleDao).favoriteNumIncrease(eq(9L));

        ArticleFavoriteRspVO response = service.toggleFavorite(9L, 5L);

        Assertions.assertTrue(response.getFavorited());
        Assertions.assertEquals(1L, response.getFavoriteCount());
        verify(articleDao).favoriteNumIncrease(9L);
    }

    @Test
    void toggleFavoriteShouldRejectAnonymousUsers() {
        AdminArticleInteractionServiceImpl service = new AdminArticleInteractionServiceImpl();
        ReflectionTestUtils.setField(service, "articleFavoriteDao", mock(AdminArticleFavoriteDao.class));
        ReflectionTestUtils.setField(service, "articleMapper", mock(ArticleMapper.class));
        ReflectionTestUtils.setField(service, "articleDao", mock(AdminArticleDao.class));
        ReflectionTestUtils.setField(service, "articleLikeDao", mock(AdminArticleLikeDao.class));
        ReflectionTestUtils.setField(service, "userDao", mock(AdminUserDao.class));

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.toggleFavorite(1L, null));

        Assertions.assertEquals(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    void toggleLikeShouldAdjustLikeNumAtomicallyForVisitors() {
        AdminArticleInteractionServiceImpl service = new AdminArticleInteractionServiceImpl();
        AdminArticleLikeDao likeDao = mock(AdminArticleLikeDao.class);
        ArticleMapper articleMapper = mock(ArticleMapper.class);
        AdminArticleDao articleDao = mock(AdminArticleDao.class);

        ReflectionTestUtils.setField(service, "articleLikeDao", likeDao);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);
        ReflectionTestUtils.setField(service, "articleDao", articleDao);
        ReflectionTestUtils.setField(service, "articleFavoriteDao", mock(AdminArticleFavoriteDao.class));
        ReflectionTestUtils.setField(service, "userDao", mock(AdminUserDao.class));

        when(articleMapper.selectOne(any())).thenReturn(ArticleDO.builder().id(11L).likeNum(0L).build());
        when(likeDao.selectByArticleIdAndVisitorId(11L, "visitor-1")).thenReturn(null);
        when(likeDao.selectCountByArticleId(11L)).thenReturn(1L);
        doAnswer(invocation -> {
            Long articleId = invocation.getArgument(0);
            Assertions.assertEquals(11L, articleId);
            return 1;
        }).when(articleDao).likeNumIncrease(eq(11L));

        ArticleLikeRspVO response = service.toggleLike(11L, null, "visitor-1");

        Assertions.assertTrue(response.getLiked());
        Assertions.assertEquals(1L, response.getLikeCount());
        verify(articleDao).likeNumIncrease(11L);
    }
}
