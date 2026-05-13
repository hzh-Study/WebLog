package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoritePageReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoritePageRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoriteReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleFavoriteRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleLikeReqVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleLikeRspVO;
import com.quanxiaoha.weblog.admin.service.impl.AdminArticleInteractionServiceImpl;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleInteractionController {

    @Autowired
    @Qualifier("adminArticleInteractionService")
    private AdminArticleInteractionServiceImpl articleInteractionService;

    @PostMapping("/like")
    @ApiOperationLog(description = "文章点赞/取消点赞")
    public Response<ArticleLikeRspVO> like(@RequestBody ArticleLikeReqVO reqVO, HttpServletRequest request) {
        Long userId = resolveUserId();
        String visitorId = userId == null ? generateVisitorId(request) : null;
        ArticleLikeRspVO rspVO = articleInteractionService.toggleLike(reqVO.getArticleId(), userId, visitorId);
        return Response.success(rspVO);
    }

    @PostMapping("/like/status")
    @ApiOperationLog(description = "获取文章点赞状态")
    public Response<ArticleLikeRspVO> likeStatus(@RequestBody ArticleLikeReqVO reqVO, HttpServletRequest request) {
        Long userId = resolveUserId();
        String visitorId = userId == null ? generateVisitorId(request) : null;
        ArticleLikeRspVO rspVO = articleInteractionService.getLikeStatus(reqVO.getArticleId(), userId, visitorId);
        return Response.success(rspVO);
    }

    @PostMapping("/favorite")
    @ApiOperationLog(description = "文章收藏/取消收藏")
    public Response<ArticleFavoriteRspVO> favorite(@RequestBody ArticleFavoriteReqVO reqVO) {
        Long userId = resolveUserId();
        ArticleFavoriteRspVO rspVO = articleInteractionService.toggleFavorite(reqVO.getArticleId(), userId);
        return Response.success(rspVO);
    }

    @PostMapping("/favorite/list")
    @ApiOperationLog(description = "获取收藏列表")
    public Response<ArticleFavoritePageRspVO> favoriteList(@RequestBody ArticleFavoritePageReqVO reqVO) {
        Long userId = resolveUserId();
        if (userId == null) {
            return Response.fail(ResponseCodeEnum.UNAUTHORIZED);
        }
        ArticleFavoritePageRspVO rspVO = articleInteractionService.getFavoriteList(userId, reqVO.getCurrent(), reqVO.getSize());
        return Response.success(rspVO);
    }

    private Long resolveUserId() {
        try {
            String username = SecurityUtils.getCurrentUsername();
            return articleInteractionService.resolveUserId(username);
        } catch (Exception e) {
            return null;
        }
    }

    private String generateVisitorId(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        String raw = (ip != null ? ip : "") + (ua != null ? ua : "");
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}