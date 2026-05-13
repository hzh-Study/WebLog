package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminAiUsageRecordDao;
import com.quanxiaoha.weblog.common.domain.dos.AiUsageRecordDO;
import com.quanxiaoha.weblog.common.domain.mapper.AiUsageRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AdminAiUsageRecordDaoImpl implements AdminAiUsageRecordDao {

    @Autowired
    private AiUsageRecordMapper aiUsageRecordMapper;

    @Override
    public AiUsageRecordDO selectByUserIdAndDate(Long userId, Date usageDate) {
        QueryWrapper<AiUsageRecordDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(AiUsageRecordDO::getUserId, userId)
                .eq(AiUsageRecordDO::getUsageDate, usageDate);
        return aiUsageRecordMapper.selectOne(wrapper);
    }

    @Override
    public int insert(AiUsageRecordDO record) {
        return aiUsageRecordMapper.insert(record);
    }

    @Override
    public int updateById(AiUsageRecordDO record) {
        return aiUsageRecordMapper.updateById(record);
    }
}
