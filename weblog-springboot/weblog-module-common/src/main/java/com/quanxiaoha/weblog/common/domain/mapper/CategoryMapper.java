package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.PublicCatalogItemStatsDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<CategoryDO> {

    @Select("SELECT c.id, c.name, COUNT(DISTINCT a.id) AS articleCount " +
            "FROM t_category c " +
            "INNER JOIN t_article_category_rel r ON r.category_id = c.id " +
            "INNER JOIN t_article a ON a.id = r.article_id AND a.is_deleted = 0 AND a.visibility = 'PUBLIC' " +
            "WHERE c.is_deleted = 0 " +
            "GROUP BY c.id, c.name " +
            "ORDER BY articleCount DESC, c.id ASC " +
            "LIMIT #{limit}")
    List<PublicCatalogItemStatsDO> selectTopByPublicArticleCount(@Param("limit") int limit);

    @Select("SELECT c.id, c.name, COUNT(DISTINCT a.id) AS articleCount " +
            "FROM t_category c " +
            "INNER JOIN t_article_category_rel r ON r.category_id = c.id " +
            "INNER JOIN t_article a ON a.id = r.article_id AND a.is_deleted = 0 AND a.visibility = 'PUBLIC' " +
            "WHERE c.is_deleted = 0 " +
            "GROUP BY c.id, c.name " +
            "ORDER BY articleCount DESC, c.id ASC")
    List<PublicCatalogItemStatsDO> selectAllByPublicArticleCount();
}
