package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.dao.AdminArticleFavoriteDao;
import com.quanxiaoha.weblog.common.domain.dos.ArticleFavoriteDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleFavoriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class AdminArticleFavoriteDaoImpl implements AdminArticleFavoriteDao {

    @Autowired
    private ArticleFavoriteMapper articleFavoriteMapper;

    @Override
    public void insert(ArticleFavoriteDO record) {
        articleFavoriteMapper.insert(record);
    }

    @Override
    public void deleteByArticleIdAndUserId(Long articleId, Long userId) {
        QueryWrapper<ArticleFavoriteDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleFavoriteDO::getArticleId, articleId)
                .eq(ArticleFavoriteDO::getUserId, userId);
        articleFavoriteMapper.delete(wrapper);
    }

    @Override
    public ArticleFavoriteDO selectByArticleIdAndUserId(Long articleId, Long userId) {
        QueryWrapper<ArticleFavoriteDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleFavoriteDO::getArticleId, articleId)
                .eq(ArticleFavoriteDO::getUserId, userId);
        return articleFavoriteMapper.selectOne(wrapper);
    }

    @Override
    public List<ArticleFavoriteDO> selectPageByUserId(Long userId, Long current, Long size) {
        Page<ArticleFavoriteDO> page = new Page<>(current, size);
        QueryWrapper<ArticleFavoriteDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleFavoriteDO::getUserId, userId)
                .orderByDesc(ArticleFavoriteDO::getCreateTime);
        return articleFavoriteMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public Long selectCountByUserId(Long userId) {
        QueryWrapper<ArticleFavoriteDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ArticleFavoriteDO::getUserId, userId);
        return articleFavoriteMapper.selectCount(wrapper);
    }

    @Override
    public Long selectCountByArticleId(Long articleId) {
        QueryWrapper<ArticleFavoriteDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ArticleFavoriteDO::getArticleId, articleId);
        return articleFavoriteMapper.selectCount(wrapper);
    }
}