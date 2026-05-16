package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("t_article_comment")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long parentId;

    private Long rootId;

    private Long replyToCommentId;

    private Long userId;

    private String visitorId;

    private String nickname;

    private String email;

    private String website;

    private String content;

    private Integer status;

    private String ipAddress;

    private String ipRegion;

    private Date createTime;

    private Date updateTime;

    private Boolean isDeleted;
}