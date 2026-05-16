package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCommentDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleCommentMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import com.quanxiaoha.weblog.web.model.vo.comment.CommentItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentReqVO;
import com.quanxiaoha.weblog.web.model.vo.comment.PublishCommentRspVO;
import com.quanxiaoha.weblog.web.model.vo.comment.QueryCommentListReqVO;
import com.quanxiaoha.weblog.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResponse getCommentList(QueryCommentListReqVO reqVO) {
        Long articleId = reqVO.getArticleId();
        Long current = reqVO.getCurrent() != null ? reqVO.getCurrent() : 1L;
        Long size = reqVO.getSize() != null ? Math.min(reqVO.getSize(), 20L) : 10L;

        Page<ArticleCommentDO> page = new Page<>(current, size);
        IPage<ArticleCommentDO> rootPage = articleCommentMapper.selectRootCommentPage(page, articleId, 1);

        List<ArticleCommentDO> rootComments = rootPage.getRecords();
        if (CollectionUtils.isEmpty(rootComments)) {
            return PageResponse.success(rootPage, Collections.emptyList());
        }

        List<Long> rootIds = rootComments.stream()
                .map(ArticleCommentDO::getId)
                .collect(Collectors.toList());
        List<ArticleCommentDO> replies = articleCommentMapper.selectRepliesByRootIds(rootIds, 1);

        Map<Long, List<ArticleCommentDO>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(ArticleCommentDO::getRootId));

        List<CommentItemRspVO> dataList = rootComments.stream()
                .map(root -> buildCommentItem(root, replyMap))
                .collect(Collectors.toList());

        return PageResponse.success(rootPage, dataList);
    }

    @Override
    public Response publishComment(PublishCommentReqVO reqVO, HttpServletRequest request) {
        Long articleId = reqVO.getArticleId();

        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (articleDO == null || Boolean.TRUE.equals(articleDO.getIsDeleted())) {
            return Response.fail("文章不存在");
        }

        Long parentId = reqVO.getParentId();
        Long replyToCommentId = reqVO.getReplyToCommentId();
        Long rootId = null;

        if (parentId != null) {
            ArticleCommentDO parentComment = articleCommentMapper.selectById(parentId);
            if (parentComment == null || Boolean.TRUE.equals(parentComment.getIsDeleted())) {
                return Response.fail("回复的目标评论不存在");
            }
            if (!Objects.equals(parentComment.getArticleId(), articleId)) {
                return Response.fail("评论与文章不匹配");
            }
            if (parentComment.getParentId() == null) {
                rootId = parentId;
            } else {
                rootId = parentComment.getRootId();
            }
        }

        if (replyToCommentId == null && parentId != null) {
            replyToCommentId = parentId;
        }

        if (replyToCommentId != null) {
            ArticleCommentDO replyToComment = articleCommentMapper.selectById(replyToCommentId);
            if (replyToComment == null || Boolean.TRUE.equals(replyToComment.getIsDeleted())) {
                return Response.fail("回复的目标评论不存在");
            }
        }

        String nickname = reqVO.getNickname();
        Long userId = null;
        String visitorId = null;
        String ipAddress = request.getRemoteAddr();

        try {
            String username = SecurityUtils.getCurrentUsername();
            if (StringUtils.hasText(username)) {
                UserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                        .eq(UserDO::getUsername, username)
                        .eq(UserDO::getIsDeleted, false));
                if (userDO != null) {
                    userId = userDO.getId();
                    if (StringUtils.hasText(userDO.getNickname())) {
                        nickname = userDO.getNickname();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("未登录用户发表评论");
        }

        if (userId == null) {
            visitorId = generateVisitorId(request);
        }

        String content = reqVO.getContent();
        if (content != null) {
            content = content.trim();
        }

        ArticleCommentDO commentDO = ArticleCommentDO.builder()
                .articleId(articleId)
                .parentId(parentId)
                .rootId(rootId)
                .replyToCommentId(replyToCommentId)
                .userId(userId)
                .visitorId(visitorId)
                .nickname(nickname)
                .email(reqVO.getEmail())
                .website(normalizeWebsite(reqVO.getWebsite()))
                .content(content)
                .status(0)
                .ipAddress(ipAddress)
                .isDeleted(false)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        articleCommentMapper.insert(commentDO);

        PublishCommentRspVO rspVO = PublishCommentRspVO.builder()
                .message("评论已提交，审核通过后展示")
                .build();
        return Response.success(rspVO);
    }

    private CommentItemRspVO buildCommentItem(ArticleCommentDO commentDO, Map<Long, List<ArticleCommentDO>> replyMap) {
        List<CommentItemRspVO> children = new ArrayList<>();
        List<ArticleCommentDO> childDOs = replyMap.getOrDefault(commentDO.getId(), Collections.emptyList());
        for (ArticleCommentDO child : childDOs) {
            CommentItemRspVO childVO = CommentItemRspVO.builder()
                    .id(child.getId())
                    .articleId(child.getArticleId())
                    .parentId(child.getParentId())
                    .rootId(child.getRootId())
                    .replyToCommentId(child.getReplyToCommentId())
                    .nickname(child.getNickname())
                    .content(child.getContent())
                    .website(child.getWebsite())
                    .createTime(child.getCreateTime() != null ? DATE_FORMAT.format(child.getCreateTime()) : null)
                    .replyToNickname(getReplyToNickname(child.getReplyToCommentId()))
                    .children(Collections.emptyList())
                    .build();
            children.add(childVO);
        }

        return CommentItemRspVO.builder()
                .id(commentDO.getId())
                .articleId(commentDO.getArticleId())
                .parentId(commentDO.getParentId())
                .rootId(commentDO.getRootId())
                .replyToCommentId(commentDO.getReplyToCommentId())
                .nickname(commentDO.getNickname())
                .content(commentDO.getContent())
                .website(commentDO.getWebsite())
                .createTime(commentDO.getCreateTime() != null ? DATE_FORMAT.format(commentDO.getCreateTime()) : null)
                .replyToNickname(getReplyToNickname(commentDO.getReplyToCommentId()))
                .children(children)
                .build();
    }

    private String getReplyToNickname(Long replyToCommentId) {
        if (replyToCommentId == null) {
            return null;
        }
        ArticleCommentDO target = articleCommentMapper.selectById(replyToCommentId);
        return target != null ? target.getNickname() : null;
    }

    private String generateVisitorId(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        String raw = (ip != null ? ip : "") + (ua != null ? ua : "");
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String normalizeWebsite(String website) {
        if (!StringUtils.hasText(website)) {
            return null;
        }
        String trimmed = website.trim();
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }
}