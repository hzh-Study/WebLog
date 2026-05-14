package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quanxiaoha.weblog.common.domain.dos.ArticleHotScoreDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleHotScoreMapper extends BaseMapper<ArticleHotScoreDO> {

    @Select("SELECT * FROM t_article_hot_score ORDER BY hot_score DESC LIMIT #{limit}")
    List<ArticleHotScoreDO> selectHotArticles(@Param("limit") int limit);

    @Select("SELECT * FROM t_article_hot_score ORDER BY hot_score DESC LIMIT #{offset}, #{limit}")
    List<ArticleHotScoreDO> selectHotArticlesPage(@Param("offset") int offset, @Param("limit") int limit);
}
