package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminArticleLikeDao;
import com.quanxiaoha.weblog.common.domain.dos.ArticleLikeDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleLikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AdminArticleLikeDaoImpl implements AdminArticleLikeDao {

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Override
    public void insert(ArticleLikeDO record) {
        articleLikeMapper.insert(record);
    }

    @Override
    public void deleteByArticleIdAndUserId(Long articleId, Long userId) {
        QueryWrapper<ArticleLikeDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleLikeDO::getArticleId, articleId)
                .eq(ArticleLikeDO::getUserId, userId);
        articleLikeMapper.delete(wrapper);
    }

    @Override
    public void deleteByArticleIdAndVisitorId(Long articleId, String visitorId) {
        QueryWrapper<ArticleLikeDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleLikeDO::getArticleId, articleId)
                .eq(ArticleLikeDO::getVisitorId, visitorId);
        articleLikeMapper.delete(wrapper);
    }

    @Override
    public ArticleLikeDO selectByArticleIdAndUserId(Long articleId, Long userId) {
        QueryWrapper<ArticleLikeDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleLikeDO::getArticleId, articleId)
                .eq(ArticleLikeDO::getUserId, userId);
        return articleLikeMapper.selectOne(wrapper);
    }

    @Override
    public ArticleLikeDO selectByArticleIdAndVisitorId(Long articleId, String visitorId) {
        QueryWrapper<ArticleLikeDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleLikeDO::getArticleId, articleId)
                .eq(ArticleLikeDO::getVisitorId, visitorId);
        return articleLikeMapper.selectOne(wrapper);
    }

    @Override
    public Long selectCountByArticleId(Long articleId) {
        QueryWrapper<ArticleLikeDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ArticleLikeDO::getArticleId, articleId);
        return articleLikeMapper.selectCount(wrapper);
    }
}