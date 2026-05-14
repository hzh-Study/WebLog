package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("t_category")
public class CategoryDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long parentId;
    private String name;
    private String code;
    private Integer level;
    private Integer sort;
    private Boolean isSystem;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
}
