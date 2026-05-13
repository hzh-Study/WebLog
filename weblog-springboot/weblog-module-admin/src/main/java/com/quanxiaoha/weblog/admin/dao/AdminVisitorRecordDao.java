package com.quanxiaoha.weblog.admin.dao;

import com.quanxiaoha.weblog.common.domain.dos.VisitorRecordDO;

import java.util.List;
import java.util.Map;

public interface AdminVisitorRecordDao {

    List<VisitorRecordDO> selectListByDateRange(String startDate, String endDate);

    Long selectDistinctVisitorCountByDate(String date);

    List<Map<String, Object>> selectRegionCount();
}