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
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_ai_usage_record")
public class AiUsageRecordDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Date usageDate;
    private Integer articleGenCount;
    private Long tokenUsed;
    private Date lastInteractionTime;
}
