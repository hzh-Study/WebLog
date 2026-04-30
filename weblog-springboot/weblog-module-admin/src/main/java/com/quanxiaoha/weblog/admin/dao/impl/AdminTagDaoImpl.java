package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.admin.dao.AdminTagDao;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
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
public class AdminTagDaoImpl implements AdminTagDao {
    @Autowired
    private TagMapper tagMapper;

    @Override
    public Page<TagDO> queryTagPageList(Long current, Long size, Date startDate, Date endDate, String tagName, Long userId) {
        Page<TagDO> page = new Page<>(current, size);
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0)
                .like(Objects.nonNull(tagName), TagDO::getName, tagName)
                .ge(Objects.nonNull(startDate), TagDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), TagDO::getCreateTime, endDate)
                .orderByDesc(TagDO::getCreateTime);
        return tagMapper.selectPage(page, wrapper);
    }

    @Override
    public List<TagDO> searchTags(String key, Long userId) {
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0)
                .apply("NAME like UPPER(CONCAT('%', {0}, '%')) OR NAME LIKE LOWER(CONCAT('%', {0}, '%'))", key)
                .orderByAsc(TagDO::getName);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public List<TagDO> selectAllByUserId(Long userId) {
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public int insert(TagDO tagDO) {
        return tagMapper.insert(tagDO);
    }

    @Override
    public Long selectTotalCount(Long userId) {
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.select("1").lambda()
                .eq(TagDO::getUserId, userId)
                .eq(TagDO::getIsDeleted, 0);
        return tagMapper.selectCount(wrapper);
    }
}
