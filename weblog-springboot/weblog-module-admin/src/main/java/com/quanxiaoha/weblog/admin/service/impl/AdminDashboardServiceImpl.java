package com.quanxiaoha.weblog.admin.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminCategoryDao;
import com.quanxiaoha.weblog.admin.dao.AdminStatisticsUserPVDao;
import com.quanxiaoha.weblog.admin.dao.AdminTagDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.QueryDashboardArticleStatisticsRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.QueryDashboardPVStatisticsRspVO;
import com.quanxiaoha.weblog.admin.service.AdminDashboardService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.Constants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCountDO;
import com.quanxiaoha.weblog.common.domain.dos.StatisticsUserPVDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 仪表盘：当前登录用户维度的内容统计
 */
@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private AdminArticleDao articleDao;
    @Autowired
    private AdminCategoryDao categoryDao;
    @Autowired
    private AdminTagDao tagDao;
    @Autowired
    private AdminStatisticsUserPVDao statisticsUserPVDao;
    @Autowired
    private AdminUserDao userDao;

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO u = userDao.selectByUsername(username);
        if (u == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return u;
    }

    @Override
    public Response queryDashboardArticleStatisticsInfo() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();
        Long articleTotalCount = articleDao.selectTotalCountByUserId(userId);
        Long categoryTotalCount = categoryDao.selectTotalCount(userId);
        Long tagTotalCount = tagDao.selectTotalCount(userId);
        Long readSum = articleDao.sumReadNumByUserId(userId);
        if (readSum == null) {
            readSum = 0L;
        }

        QueryDashboardArticleStatisticsRspVO vo = QueryDashboardArticleStatisticsRspVO.builder()
                .articleTotalCount(Objects.isNull(articleTotalCount) ? 0 : articleTotalCount)
                .categoryTotalCount(Objects.isNull(categoryTotalCount) ? 0 : categoryTotalCount)
                .tagTotalCount(Objects.isNull(tagTotalCount) ? 0 : tagTotalCount)
                .pvTotalCount(readSum)
                .build();
        return Response.success(vo);
    }

    @Override
    public Response queryDashboardPublishArticleStatisticsInfo() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfRange = currentDate.minusMonths(5).withDayOfMonth(1);
        String firstDayStr = firstDayOfRange.format(Constants.DATE_TIME_FORMATTER);
        String currDayStr = currentDate.format(Constants.DATE_TIME_FORMATTER);

        List<ArticleCountDO> articleCountDOS = articleDao.selectArticleCountByUserId(userId, firstDayStr, currDayStr);
        Map<String, Long> map = Maps.newLinkedHashMap();
        Map<String, Long> dateCountMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(articleCountDOS)) {
            dateCountMap = articleCountDOS.stream()
                    .collect(Collectors.toMap(ArticleCountDO::getDate, ArticleCountDO::getCount, (a, b) -> a));
        }
        for (LocalDate date = firstDayOfRange; !date.isAfter(currentDate); date = date.plusDays(1)) {
            String key = date.format(Constants.DATE_TIME_FORMATTER);
            map.put(key, Objects.isNull(dateCountMap.get(key)) ? 0L : dateCountMap.get(key));
        }
        return Response.success(map);
    }

    @Override
    public Response queryDashboardPVStatisticsInfo() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();
        List<String> pvDates = Lists.newArrayList();
        List<Long> pvCounts = Lists.newArrayList();
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate minus7date = LocalDate.now().minusDays(7);
        LocalDate startDay = null;
        LocalDate endDay = null;
        for (int i = 1; i < 8; i++) {
            LocalDate d = minus7date.plusDays(i);
            if (i == 1) {
                startDay = d;
            }
            endDay = d;
            pvDates.add(d.format(outFmt));
        }
        if (startDay == null || endDay == null) {
            QueryDashboardPVStatisticsRspVO empty = QueryDashboardPVStatisticsRspVO.builder()
                    .pvDates(pvDates)
                    .pvCounts(Lists.newArrayList())
                    .build();
            return Response.success(empty);
        }
        List<StatisticsUserPVDO> rows = statisticsUserPVDao.selectByUserIdAndDateRange(
                userId, startDay.format(ymd), endDay.format(ymd));
        Map<String, Long> byMd = rows.stream()
                .collect(Collectors.toMap(
                        r -> new SimpleDateFormat("MM-dd").format(r.getPvDate()),
                        StatisticsUserPVDO::getPvCount,
                        (a, b) -> a + b
                ));
        for (int i = 1; i < 8; i++) {
            LocalDate d = minus7date.plusDays(i);
            String key = d.format(outFmt);
            pvCounts.add(byMd.getOrDefault(key, 0L));
        }
        QueryDashboardPVStatisticsRspVO vo = QueryDashboardPVStatisticsRspVO.builder()
                .pvDates(pvDates)
                .pvCounts(pvCounts)
                .build();
        return Response.success(vo);
    }
}
