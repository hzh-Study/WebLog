package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCountDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminArticleDaoImpl implements AdminArticleDao {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public int insertArticle(ArticleDO articleDO) {
        return articleMapper.insert(articleDO);
    }

    @Override
    public ArticleDO queryByArticleId(Long articleId, Long userId) {
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getId, articleId)
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, 0);
        return articleMapper.selectOne(wrapper);
    }

    @Override
    public Page<ArticleDO> queryArticlePageList(Long current, Long size, Date startDate, Date endDate, String searchTitle, Long userId) {
        Page<ArticleDO> page = new Page<>(current, size);
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, 0)
                .like(Objects.nonNull(searchTitle), ArticleDO::getTitle, searchTitle)
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)
                .orderByDesc(ArticleDO::getCreateTime);
        return articleMapper.selectPage(page, wrapper);
    }

    @Override
    public int deleteById(Long articleId) {
        return articleMapper.deleteById(articleId);
    }

    @Override
    public int updateById(ArticleDO articleDO) {
        return articleMapper.updateById(articleDO);
    }

    @Override
    public Long selectTotalCount() {
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.select("1").lambda().eq(ArticleDO::getIsDeleted, 0);
        return articleMapper.selectCount(wrapper);
    }

    @Override
    public Long selectTotalCountByUserId(Long userId) {
        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, 0);
        return articleMapper.selectCount(wrapper);
    }

    @Override
    public List<ArticleCountDO> selectArticleCount(String startDate, String endDate) {
        return articleMapper.selectArticleCount(startDate, endDate);
    }

    @Override
    public List<ArticleCountDO> selectArticleCountByUserId(Long userId, String startDate, String endDate) {
        return articleMapper.selectArticleCountByUserId(userId, startDate, endDate);
    }

    @Override
    public Long sumReadNumByUserId(Long userId) {
        return articleMapper.sumReadNumByUserId(userId);
    }

    @Override
    public int readNumIncrease(Long articleId) {
        UpdateWrapper<ArticleDO> wrapper = new UpdateWrapper<>();
        wrapper.lambda().setSql("read_num = read_num + 1").eq(ArticleDO::getId, articleId);
        return articleMapper.update(null, wrapper);
    }

    @Override
    public int likeNumIncrease(Long articleId) {
        UpdateWrapper<ArticleDO> wrapper = new UpdateWrapper<>();
        wrapper.lambda().setSql("like_num = COALESCE(like_num, 0) + 1").eq(ArticleDO::getId, articleId);
        return articleMapper.update(null, wrapper);
    }

    @Override
    public int likeNumDecrease(Long articleId) {
        UpdateWrapper<ArticleDO> wrapper = new UpdateWrapper<>();
        wrapper.lambda().setSql("like_num = GREATEST(COALESCE(like_num, 0) - 1, 0)").eq(ArticleDO::getId, articleId);
        return articleMapper.update(null, wrapper);
    }

    @Override
    public int favoriteNumIncrease(Long articleId) {
        UpdateWrapper<ArticleDO> wrapper = new UpdateWrapper<>();
        wrapper.lambda().setSql("favorite_num = COALESCE(favorite_num, 0) + 1").eq(ArticleDO::getId, articleId);
        return articleMapper.update(null, wrapper);
    }

    @Override
    public int favoriteNumDecrease(Long articleId) {
        UpdateWrapper<ArticleDO> wrapper = new UpdateWrapper<>();
        wrapper.lambda().setSql("favorite_num = GREATEST(COALESCE(favorite_num, 0) - 1, 0)").eq(ArticleDO::getId, articleId);
        return articleMapper.update(null, wrapper);
    }

    @Override
    public Long selectUserIdByArticleId(Long articleId) {
        ArticleDO a = articleMapper.selectById(articleId);
        return a == null ? null : a.getUserId();
    }
}
