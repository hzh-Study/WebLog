package com.quanxiaoha.weblog.admin.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.admin.dao.AdminRecommendDao;
import com.quanxiaoha.weblog.common.domain.dos.RecommendConfigDO;
import com.quanxiaoha.weblog.common.domain.dos.UserBehaviorLogDO;
import com.quanxiaoha.weblog.common.domain.dos.RecommendFeedbackDO;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendConfigMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserBehaviorLogMapper;
import com.quanxiaoha.weblog.common.domain.mapper.RecommendFeedbackMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdminRecommendDaoImpl implements AdminRecommendDao {

    @Autowired
    private RecommendConfigMapper recommendConfigMapper;

    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    @Autowired
    private RecommendFeedbackMapper recommendFeedbackMapper;

    @Override
    public List<RecommendConfigDO> queryAllConfigs() {
        return recommendConfigMapper.selectList(null);
    }

    @Override
    public int updateConfig(RecommendConfigDO configDO) {
        return recommendConfigMapper.updateById(configDO);
    }

    @Override
    public Long countExposuresToday() {
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LambdaQueryWrapper<UserBehaviorLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehaviorLogDO::getEventType, "exposure")
                .ge(UserBehaviorLogDO::getCreateTime, startOfDay);
        return userBehaviorLogMapper.selectCount(wrapper);
    }

    @Override
    public Long countClicksToday() {
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LambdaQueryWrapper<UserBehaviorLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehaviorLogDO::getEventType, "click")
                .ge(UserBehaviorLogDO::getCreateTime, startOfDay);
        return userBehaviorLogMapper.selectCount(wrapper);
    }

    @Override
    public Long countFeedbacksToday() {
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LambdaQueryWrapper<RecommendFeedbackDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(RecommendFeedbackDO::getCreateTime, startOfDay);
        return recommendFeedbackMapper.selectCount(wrapper);
    }

    @Override
    public Long countActiveUsers() {
        Date sevenDaysAgo = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        QueryWrapper<UserBehaviorLogDO> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(DISTINCT user_id) as cnt")
                .ge("create_time", sevenDaysAgo);
        List<Map<String, Object>> maps = userBehaviorLogMapper.selectMaps(wrapper);
        if (maps != null && !maps.isEmpty() && maps.get(0).get("cnt") != null) {
            return ((Number) maps.get(0).get("cnt")).longValue();
        }
        return 0L;
    }
}
