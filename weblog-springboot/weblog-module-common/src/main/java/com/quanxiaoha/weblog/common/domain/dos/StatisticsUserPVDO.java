package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("t_statistics_user_pv")
public class StatisticsUserPVDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Date pvDate;
    private Long pvCount;
    private Date createTime;
    private Date updateTime;
}
