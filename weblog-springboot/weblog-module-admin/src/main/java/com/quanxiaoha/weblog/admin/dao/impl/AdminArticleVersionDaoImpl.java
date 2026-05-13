package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminArticleVersionDao;
import com.quanxiaoha.weblog.common.domain.dos.ArticleVersionDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleVersionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class AdminArticleVersionDaoImpl implements AdminArticleVersionDao {

    @Autowired
    private ArticleVersionMapper articleVersionMapper;

    @Override
    public void insert(ArticleVersionDO version) {
        articleVersionMapper.insert(version);
    }

    @Override
    public Integer selectMaxVersionNum(Long articleId) {
        QueryWrapper<ArticleVersionDO> wrapper = new QueryWrapper<>();
        wrapper.select("MAX(version_num) AS versionNum")
                .lambda()
                .eq(ArticleVersionDO::getArticleId, articleId);
        ArticleVersionDO result = articleVersionMapper.selectOne(wrapper);
        return result != null ? result.getVersionNum() : 0;
    }

    @Override
    public List<ArticleVersionDO> selectListByArticleId(Long articleId) {
        QueryWrapper<ArticleVersionDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleVersionDO::getArticleId, articleId)
                .orderByDesc(ArticleVersionDO::getVersionNum);
        return articleVersionMapper.selectList(wrapper);
    }

    @Override
    public ArticleVersionDO selectById(Long id) {
        return articleVersionMapper.selectById(id);
    }

    @Override
    public void deleteOldestExceed(Long articleId, Integer keepCount) {
        List<ArticleVersionDO> versions = selectListByArticleId(articleId);
        if (versions.size() > keepCount) {
            List<Long> idsToDelete = versions.stream()
                    .skip(keepCount)
                    .map(ArticleVersionDO::getId)
                    .collect(Collectors.toList());
            articleVersionMapper.deleteBatchIds(idsToDelete);
        }
    }
}