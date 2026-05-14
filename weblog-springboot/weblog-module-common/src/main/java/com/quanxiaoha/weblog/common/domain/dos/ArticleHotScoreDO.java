package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@TableName("t_article_hot_score")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleHotScoreDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private BigDecimal hotScore;
    private Integer readCount24h;
    private Integer interactionCount24h;
    private Date calculatedAt;
}
