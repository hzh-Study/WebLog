package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCountDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<ArticleDO> {

    @Select("<script>" +
            "SELECT a.* " +
            "FROM t_article a " +
            "LEFT JOIN t_user u ON u.id = a.user_id AND u.is_deleted = 0 " +
            "WHERE a.is_deleted = 0 AND a.visibility = #{visibility} " +
            "<if test='userId != null'>AND a.user_id = #{userId} </if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (" +
            "a.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR a.description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR EXISTS (SELECT 1 FROM t_article_category_rel acr JOIN t_category c ON c.id = acr.category_id AND c.is_deleted = 0 WHERE acr.article_id = a.id AND c.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "OR EXISTS (SELECT 1 FROM t_article_tag_rel atr JOIN t_tag t ON t.id = atr.tag_id AND t.is_deleted = 0 WHERE atr.article_id = a.id AND t.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "OR EXISTS (SELECT 1 FROM t_article_content ac WHERE ac.article_id = a.id AND ac.content LIKE CONCAT('%', #{keyword}, '%'))" +
            ") " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "ORDER BY CASE " +
            "WHEN a.title LIKE CONCAT('%', #{keyword}, '%') OR a.description LIKE CONCAT('%', #{keyword}, '%') THEN 0 " +
            "WHEN u.username LIKE CONCAT('%', #{keyword}, '%') OR u.nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR EXISTS (SELECT 1 FROM t_article_category_rel acr JOIN t_category c ON c.id = acr.category_id AND c.is_deleted = 0 WHERE acr.article_id = a.id AND c.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "OR EXISTS (SELECT 1 FROM t_article_tag_rel atr JOIN t_tag t ON t.id = atr.tag_id AND t.is_deleted = 0 WHERE atr.article_id = a.id AND t.name LIKE CONCAT('%', #{keyword}, '%')) THEN 1 " +
            "WHEN EXISTS (SELECT 1 FROM t_article_content ac WHERE ac.article_id = a.id AND ac.content LIKE CONCAT('%', #{keyword}, '%')) THEN 2 " +
            "ELSE 3 END, a.create_time DESC" +
            "</if>" +
            "<if test='keyword == null or keyword == \"\"'>ORDER BY a.create_time DESC</if>" +
            "</script>")
    IPage<ArticleDO> searchPublicArticlePage(Page<ArticleDO> page,
                                             @Param("keyword") String keyword,
                                             @Param("userId") Long userId,
                                             @Param("visibility") String visibility);

    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count\n" +
            "FROM t_article\n" +
            "WHERE create_time >= #{startDate} AND create_time <= #{endDate}\n" +
            "GROUP BY DATE(create_time)\n" +
            "ORDER BY DATE(create_time)")
    List<ArticleCountDO> selectArticleCount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count\n" +
            "FROM t_article\n" +
            "WHERE user_id = #{userId} AND is_deleted = 0\n" +
            "AND create_time >= #{startDate} AND create_time <= #{endDate}\n" +
            "GROUP BY DATE(create_time)\n" +
            "ORDER BY DATE(create_time)")
    List<ArticleCountDO> selectArticleCountByUserId(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(SUM(read_num), 0) FROM t_article WHERE user_id = #{userId} AND is_deleted = 0")
    Long sumReadNumByUserId(@Param("userId") Long userId);
}
