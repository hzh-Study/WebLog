package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminVisitorRecordDao;
import com.quanxiaoha.weblog.common.domain.dos.VisitorRecordDO;
import com.quanxiaoha.weblog.common.domain.mapper.VisitorRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AdminVisitorRecordDaoImpl implements AdminVisitorRecordDao {

    @Autowired
    private VisitorRecordMapper visitorRecordMapper;

    @Override
    public List<VisitorRecordDO> selectListByDateRange(String startDate, String endDate) {
        QueryWrapper<VisitorRecordDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .apply("visit_time >= {0} AND visit_time <= {1}", startDate, endDate)
                .orderByDesc(VisitorRecordDO::getVisitTime);
        return visitorRecordMapper.selectList(wrapper);
    }

    @Override
    public Long selectDistinctVisitorCountByDate(String date) {
        QueryWrapper<VisitorRecordDO> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT visitor) AS distinctCount")
                .lambda()
                .apply("DATE(visit_time) = {0}", date);
        List<Map<String, Object>> result = visitorRecordMapper.selectMaps(wrapper);
        if (result != null && !result.isEmpty()) {
            Object count = result.get(0).get("distinctCount");
            return count != null ? Long.valueOf(count.toString()) : 0L;
        }
        return 0L;
    }

    @Override
    public List<Map<String, Object>> selectRegionCount() {
        QueryWrapper<VisitorRecordDO> wrapper = new QueryWrapper<>();
        wrapper.select("ip_region, COUNT(*) AS count")
                .groupBy("ip_region")
                .orderByDesc("count");
        return visitorRecordMapper.selectMaps(wrapper);
    }
}