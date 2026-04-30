package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.StatisticsUserPVDO;

import java.util.Date;
import java.util.List;

public interface AdminStatisticsUserPVDao {

    void pvIncrease(Long userId, Date currDate);

    /**
     * 按日期区间查询（含端点），按 pv_date 升序
     */
    List<StatisticsUserPVDO> selectByUserIdAndDateRange(Long userId, String startYmd, String endYmd);
}
