package com.quanxiaoha.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("t_user_behavior_log")
public class UserBehaviorLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String eventType;
    private Long targetId;
    private String targetType;
    private String eventData;
    private Long duration;
    private String referrer;
    private Date createTime;
}
