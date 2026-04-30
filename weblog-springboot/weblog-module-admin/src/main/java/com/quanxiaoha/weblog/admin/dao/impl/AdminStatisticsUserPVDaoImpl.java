package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminStatisticsUserPVDao;
import com.quanxiaoha.weblog.common.domain.dos.StatisticsUserPVDO;
import com.quanxiaoha.weblog.common.domain.mapper.StatisticsUserPVMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminStatisticsUserPVDaoImpl implements AdminStatisticsUserPVDao {

    @Autowired
    private StatisticsUserPVMapper statisticsUserPVMapper;

    @Override
    public void pvIncrease(Long userId, Date currDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(currDate);

        QueryWrapper<StatisticsUserPVDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StatisticsUserPVDO::getUserId, userId)
                .apply("pv_date = {0}", dateStr);
        Long count = statisticsUserPVMapper.selectCount(queryWrapper);

        if (count > 0) {
            UpdateWrapper<StatisticsUserPVDO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .setSql("pv_count = pv_count + 1")
                    .eq(StatisticsUserPVDO::getUserId, userId)
                    .apply("pv_date = {0}", dateStr);
            statisticsUserPVMapper.update(null, updateWrapper);
        } else {
            StatisticsUserPVDO row = StatisticsUserPVDO.builder()
                    .userId(userId)
                    .pvCount(1L)
                    .pvDate(currDate)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            statisticsUserPVMapper.insert(row);
        }
    }

    @Override
    public List<StatisticsUserPVDO> selectByUserIdAndDateRange(Long userId, String startYmd, String endYmd) {
        QueryWrapper<StatisticsUserPVDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(StatisticsUserPVDO::getUserId, userId)
                .apply("pv_date >= {0} AND pv_date <= {1}", startYmd, endYmd)
                .orderByAsc(StatisticsUserPVDO::getPvDate);
        return statisticsUserPVMapper.selectList(wrapper);
    }
}
