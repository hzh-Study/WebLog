package com.quanxiaoha.weblog.web.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.web.dao.ArticleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class ArticleDaoImpl implements ArticleDao {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public IPage<ArticleDO> queryArticlePageList(long current, long size, String keyword, Long userId) {
        Page<ArticleDO> page = new Page<>(current, size);
        String k = StringUtils.hasText(keyword) ? keyword.trim() : null;
        return articleMapper.searchPublicArticlePage(page, k, userId, ArticleVisibilityConstants.PUBLIC);
    }

    @Override
    public ArticleDO selectArticleById(Long articleId) {
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getId, articleId)
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC)
                .last("limit 1");
        return articleMapper.selectOne(wrapper);
    }

    @Override
    public ArticleDO selectPreArticle(Long articleId) {
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC)
                .orderByAsc(ArticleDO::getId)
                .gt(ArticleDO::getId, articleId)
                .last("limit 1");
        return articleMapper.selectOne(wrapper);
    }

    @Override
    public ArticleDO selectNextArticle(Long articleId) {
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC)
                .orderByDesc(ArticleDO::getId)
                .lt(ArticleDO::getId, articleId)
                .last("limit 1");
        return articleMapper.selectOne(wrapper);
    }

    @Override
    public IPage<ArticleDO> queryArticlePageListByArticleIds(Long current, Long size, List<Long> articleIds, Long userId) {
        Page<ArticleDO> page = new Page<>(current, size);
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(ArticleDO::getId, articleIds)
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC)
                .eq(userId != null, ArticleDO::getUserId, userId)
                .orderByDesc(ArticleDO::getCreateTime);
        return articleMapper.selectPage(page, wrapper);
    }
}
