package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("t_tag")
public class TagDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private String name;
    private String code;
    private String aliasJson;
    private Integer sort;
    private Boolean isSystem;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
}
