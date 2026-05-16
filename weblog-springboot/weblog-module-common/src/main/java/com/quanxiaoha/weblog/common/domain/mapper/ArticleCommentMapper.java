package com.quanxiaoha.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCommentDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleCommentMapper extends BaseMapper<ArticleCommentDO> {

    @Select("SELECT c.* FROM t_article_comment c " +
            "WHERE c.article_id = #{articleId} AND c.parent_id IS NULL " +
            "AND c.status = #{status} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<ArticleCommentDO> selectRootCommentPage(Page<ArticleCommentDO> page,
                                                  @Param("articleId") Long articleId,
                                                  @Param("status") Integer status);

    @Select("<script>" +
            "SELECT c.* FROM t_article_comment c " +
            "WHERE c.root_id IN " +
            "<foreach collection='rootIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "AND c.status = #{status} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC" +
            "</script>")
    List<ArticleCommentDO> selectRepliesByRootIds(@Param("rootIds") List<Long> rootIds,
                                                   @Param("status") Integer status);

    @Select("<script>" +
            "SELECT c.* FROM t_article_comment c " +
            "WHERE c.is_deleted = 0 " +
            "<if test='status != null'>AND c.status = #{status} </if>" +
            "<if test='nickname != null and nickname != \"\"'>AND c.nickname LIKE CONCAT('%', #{nickname}, '%') </if>" +
            "<if test='articleId != null'>AND c.article_id = #{articleId} </if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<ArticleCommentDO> selectAdminCommentPage(Page<ArticleCommentDO> page,
                                                    @Param("status") Integer status,
                                                    @Param("nickname") String nickname,
                                                    @Param("articleId") Long articleId);
}