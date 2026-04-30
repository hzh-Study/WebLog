package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.PublicCatalogItemStatsDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TagMapper extends BaseMapper<TagDO> {

    @Select("SELECT t.id, t.name, COUNT(DISTINCT a.id) AS articleCount " +
            "FROM t_tag t " +
            "INNER JOIN t_article_tag_rel r ON r.tag_id = t.id " +
            "INNER JOIN t_article a ON a.id = r.article_id AND a.is_deleted = 0 AND a.visibility = 'PUBLIC' " +
            "WHERE t.is_deleted = 0 " +
            "GROUP BY t.id, t.name " +
            "ORDER BY articleCount DESC, t.id ASC " +
            "LIMIT #{limit}")
    List<PublicCatalogItemStatsDO> selectTopByPublicArticleCount(@Param("limit") int limit);

    @Select("SELECT t.id, t.name, COUNT(DISTINCT a.id) AS articleCount " +
            "FROM t_tag t " +
            "INNER JOIN t_article_tag_rel r ON r.tag_id = t.id " +
            "INNER JOIN t_article a ON a.id = r.article_id AND a.is_deleted = 0 AND a.visibility = 'PUBLIC' " +
            "WHERE t.is_deleted = 0 " +
            "GROUP BY t.id, t.name " +
            "ORDER BY articleCount DESC, t.id ASC")
    List<PublicCatalogItemStatsDO> selectAllByPublicArticleCount();
}
