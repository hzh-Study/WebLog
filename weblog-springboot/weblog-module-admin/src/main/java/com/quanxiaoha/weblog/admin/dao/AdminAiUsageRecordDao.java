package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.AiUsageRecordDO;

import java.util.Date;

public interface AdminAiUsageRecordDao {

    AiUsageRecordDO selectByUserIdAndDate(Long userId, Date usageDate);

    int insert(AiUsageRecordDO record);

    int updateById(AiUsageRecordDO record);
}
