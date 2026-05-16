package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.model.vo.comment.DeleteCommentReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.QueryCommentPageItemRspVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.QueryCommentPageListReqVO;
import com.quanxiaoha.weblog.admin.model.vo.comment.ReviewCommentReqVO;
import com.quanxiaoha.weblog.admin.service.AdminCommentService;
import com.quanxiaoha.weblog.common.PageResponse;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCommentDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleCommentMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response queryCommentPageList(QueryCommentPageListReqVO reqVO) {
        Long current = reqVO.getCurrent() != null ? reqVO.getCurrent() : 1L;
        Long size = reqVO.getSize() != null ? reqVO.getSize() : 10L;
        Integer status = reqVO.getStatus();
        String nickname = reqVO.getNickname();
        String articleTitle = reqVO.getArticleTitle();

        Long articleId = null;
        if (StringUtils.hasText(articleTitle)) {
            List<ArticleDO> articles = articleMapper.selectList(new LambdaQueryWrapper<ArticleDO>()
                    .like(ArticleDO::getTitle, articleTitle)
                    .eq(ArticleDO::getIsDeleted, false));
            if (CollectionUtils.isEmpty(articles)) {
                return PageResponse.success(null, Collections.emptyList());
            }
            articleId = articles.get(0).getId();
        }

        Page<ArticleCommentDO> page = new Page<>(current, size);
        IPage<ArticleCommentDO> commentPage = articleCommentMapper.selectAdminCommentPage(page, status, nickname, articleId);

        List<ArticleCommentDO> records = commentPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageResponse.success(commentPage, Collections.emptyList());
        }

        Set<Long> articleIds = records.stream()
                .map(ArticleCommentDO::getArticleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> articleTitleMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(articleIds)) {
            List<ArticleDO> articleDOS = articleMapper.selectBatchIds(articleIds);
            articleTitleMap = articleDOS.stream()
                    .collect(Collectors.toMap(ArticleDO::getId, ArticleDO::getTitle, (a, b) -> a));
        }

        Map<Long, String> finalArticleTitleMap = articleTitleMap;
        List<QueryCommentPageItemRspVO> dataList = records.stream()
                .map(c -> QueryCommentPageItemRspVO.builder()
                        .id(c.getId())
                        .articleId(c.getArticleId())
                        .articleTitle(finalArticleTitleMap.get(c.getArticleId()))
                        .parentId(c.getParentId())
                        .rootId(c.getRootId())
                        .replyToCommentId(c.getReplyToCommentId())
                        .nickname(c.getNickname())
                        .content(c.getContent())
                        .status(c.getStatus())
                        .createTime(c.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.success(commentPage, dataList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response reviewComment(ReviewCommentReqVO reqVO) {
        Long commentId = reqVO.getCommentId();
        Integer status = reqVO.getStatus();

        if (status != 1 && status != 2) {
            return Response.fail("状态值无效，仅允许1(通过)或2(拒绝)");
        }

        ArticleCommentDO commentDO = articleCommentMapper.selectById(commentId);
        if (commentDO == null || Boolean.TRUE.equals(commentDO.getIsDeleted())) {
            return Response.fail("评论不存在或已删除");
        }

        commentDO.setStatus(status);
        commentDO.setUpdateTime(new Date());
        articleCommentMapper.updateById(commentDO);

        if (status == 1) {
            return Response.success("审核通过");
        } else {
            return Response.success("已拒绝评论");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response deleteComment(DeleteCommentReqVO reqVO) {
        Long commentId = reqVO.getCommentId();

        ArticleCommentDO commentDO = articleCommentMapper.selectById(commentId);
        if (commentDO == null || Boolean.TRUE.equals(commentDO.getIsDeleted())) {
            return Response.fail("评论不存在或已删除");
        }

        if (commentDO.getParentId() == null) {
            List<ArticleCommentDO> replies = articleCommentMapper.selectList(
                    new LambdaQueryWrapper<ArticleCommentDO>()
                            .eq(ArticleCommentDO::getRootId, commentId)
                            .eq(ArticleCommentDO::getIsDeleted, false));
            for (ArticleCommentDO reply : replies) {
                reply.setIsDeleted(true);
                reply.setUpdateTime(new Date());
                articleCommentMapper.updateById(reply);
            }
        }

        commentDO.setIsDeleted(true);
        commentDO.setUpdateTime(new Date());
        articleCommentMapper.updateById(commentDO);

        return Response.success("删除成功");
    }
}