package com.quanxiaoha.weblog.web.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.common.domain.mapper.TagMapper;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.web.dao.TagDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class TagDaoImpl implements TagDao {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagDO> selectAllTag() {
        QueryWrapper<TagDO> w = new QueryWrapper<>();
        w.inSql("id", "SELECT DISTINCT r.tag_id FROM t_article_tag_rel r "
                + "INNER JOIN t_article a ON a.id = r.article_id "
                + "WHERE a.is_deleted = 0 AND a.visibility = 'PUBLIC'");
        return tagMapper.selectList(w);
    }

    @Override
    public List<TagDO> selectByTagIds(List<Long> tagIds) {
        QueryWrapper<TagDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(TagDO::getId, tagIds);
        return tagMapper.selectList(wrapper);
    }
}
