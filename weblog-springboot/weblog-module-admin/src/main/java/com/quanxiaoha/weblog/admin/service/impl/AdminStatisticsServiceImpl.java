package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminCategoryDao;
import com.quanxiaoha.weblog.admin.dao.AdminStatisticsUserPVDao;
import com.quanxiaoha.weblog.admin.dao.AdminTagDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.dao.AdminVisitorRecordDao;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.ArticleRankRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.CategoryRatioRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.DashboardOverviewRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PublishHeatmapRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PvUvTrendReqVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.PvUvTrendRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.TagRatioRspVO;
import com.quanxiaoha.weblog.admin.model.vo.dashboard.VisitorRegionRspVO;
import com.quanxiaoha.weblog.admin.service.AdminStatisticsService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCategoryRelDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleCountDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleTagRelDO;
import com.quanxiaoha.weblog.common.domain.dos.CategoryDO;
import com.quanxiaoha.weblog.common.domain.dos.StatisticsUserPVDO;
import com.quanxiaoha.weblog.common.domain.dos.TagDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.VisitorRecordDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleCategoryRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.quanxiaoha.weblog.common.domain.mapper.VisitorRecordMapper;
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

@Service
@Slf4j
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private AdminArticleDao articleDao;
    @Autowired
    private AdminCategoryDao categoryDao;
    @Autowired
    private AdminTagDao tagDao;
    @Autowired
    private AdminStatisticsUserPVDao statisticsUserPVDao;
    @Autowired
    private AdminVisitorRecordDao visitorRecordDao;
    @Autowired
    private AdminUserDao userDao;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private VisitorRecordMapper visitorRecordMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MD = DateTimeFormatter.ofPattern("MM-dd");

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO u = userDao.selectByUsername(username);
        if (u == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return u;
    }

    @Override
    public Response<DashboardOverviewRspVO> getOverview() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();

        Long articleTotalCount = articleDao.selectTotalCountByUserId(userId);
        Long categoryTotalCount = categoryDao.selectTotalCount(userId);
        Long tagTotalCount = tagDao.selectTotalCount(userId);
        Long readSum = articleDao.sumReadNumByUserId(userId);
        if (readSum == null) {
            readSum = 0L;
        }

        String today = LocalDate.now().format(YMD);
        List<StatisticsUserPVDO> todayPvRows = statisticsUserPVDao.selectByUserIdAndDateRange(userId, today, today);
        Long todayPv = 0L;
        if (!CollectionUtils.isEmpty(todayPvRows)) {
            todayPv = todayPvRows.stream().mapToLong(StatisticsUserPVDO::getPvCount).sum();
        }

        Long todayUv = visitorRecordDao.selectDistinctVisitorCountByDate(today);

        QueryWrapper<VisitorRecordDO> uvWrapper = new QueryWrapper<>();
        uvWrapper.select("COUNT(DISTINCT visitor) AS distinctCount");
        List<Map<String, Object>> uvResult = visitorRecordMapper.selectMaps(uvWrapper);
        Long uvTotalCount = 0L;
        if (uvResult != null && !uvResult.isEmpty()) {
            Object count = uvResult.get(0).get("distinctCount");
            uvTotalCount = count != null ? Long.valueOf(count.toString()) : 0L;
        }

        DashboardOverviewRspVO vo = DashboardOverviewRspVO.builder()
                .articleTotalCount(Objects.isNull(articleTotalCount) ? 0 : articleTotalCount)
                .categoryTotalCount(Objects.isNull(categoryTotalCount) ? 0 : categoryTotalCount)
                .tagTotalCount(Objects.isNull(tagTotalCount) ? 0 : tagTotalCount)
                .pvTotalCount(readSum)
                .uvTotalCount(uvTotalCount)
                .todayPv(todayPv)
                .todayUv(todayUv)
                .build();
        return Response.success(vo);
    }

    @Override
    public Response<PvUvTrendRspVO> getPvUvTrend(PvUvTrendReqVO reqVO) {
        UserDO u = getCurrentUser();
        Long userId = u.getId();

        int days = resolvePeriodDays(reqVO.getPeriod());
        LocalDate endDay = LocalDate.now().minusDays(1);
        LocalDate startDay = endDay.minusDays(days - 1);

        List<String> dates = Lists.newArrayList();
        List<Long> pvData = Lists.newArrayList();
        List<Long> uvData = Lists.newArrayList();

        for (LocalDate d = startDay; !d.isAfter(endDay); d = d.plusDays(1)) {
            dates.add(d.format(MD));
        }

        List<StatisticsUserPVDO> rows = statisticsUserPVDao.selectByUserIdAndDateRange(
                userId, startDay.format(YMD), endDay.format(YMD));
        Map<String, Long> pvByDate = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(rows)) {
            for (StatisticsUserPVDO row : rows) {
                String key = new SimpleDateFormat("MM-dd").format(row.getPvDate());
                pvByDate.merge(key, row.getPvCount(), Long::sum);
            }
        }

        Map<String, Long> uvCache = Maps.newHashMap();
        for (LocalDate d = startDay; !d.isAfter(endDay); d = d.plusDays(1)) {
            String ymd = d.format(YMD);
            String md = d.format(MD);
            pvData.add(pvByDate.getOrDefault(md, 0L));
            if (!uvCache.containsKey(ymd)) {
                uvCache.put(ymd, visitorRecordDao.selectDistinctVisitorCountByDate(ymd));
            }
            uvData.add(uvCache.getOrDefault(ymd, 0L));
        }

        PvUvTrendRspVO vo = PvUvTrendRspVO.builder()
                .dates(dates)
                .pvData(pvData)
                .uvData(uvData)
                .build();
        return Response.success(vo);
    }

    @Override
    public Response<ArticleRankRspVO> getArticleRank() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();

        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false)
                .orderByDesc(ArticleDO::getReadNum)
                .last("LIMIT 10");
        List<ArticleDO> articles = articleMapper.selectList(wrapper);

        long totalReadNum = 0L;
        if (!CollectionUtils.isEmpty(articles)) {
            totalReadNum = articles.stream()
                    .mapToLong(a -> a.getReadNum() == null ? 0L : a.getReadNum())
                    .sum();
        }

        List<ArticleRankRspVO.ArticleRankItemVO> items = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(articles)) {
            for (ArticleDO a : articles) {
                long readNum = a.getReadNum() == null ? 0L : a.getReadNum();
                double percentage = totalReadNum > 0 ? (double) readNum / totalReadNum * 100.0 : 0.0;
                items.add(ArticleRankRspVO.ArticleRankItemVO.builder()
                        .articleId(a.getId())
                        .title(a.getTitle())
                        .pv(readNum)
                        .percentage(Math.round(percentage * 100.0) / 100.0)
                        .build());
            }
        }

        ArticleRankRspVO vo = ArticleRankRspVO.builder().items(items).build();
        return Response.success(vo);
    }

    @Override
    public Response<VisitorRegionRspVO> getVisitorRegion() {
        List<Map<String, Object>> rows = visitorRecordDao.selectRegionCount();
        List<VisitorRegionRspVO.RegionItemVO> items = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(rows)) {
            for (Map<String, Object> row : rows) {
                Object region = row.get("ip_region");
                Object count = row.get("count");
                String regionStr = region == null ? "\u672a\u77e5" : region.toString();
                Long countVal = count == null ? 0L : Long.valueOf(count.toString());
                items.add(VisitorRegionRspVO.RegionItemVO.builder()
                        .region(regionStr)
                        .count(countVal)
                        .build());
            }
        }
        VisitorRegionRspVO vo = VisitorRegionRspVO.builder().items(items).build();
        return Response.success(vo);
    }

    @Override
    public Response<PublishHeatmapRspVO> getPublishHeatmap() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(364);

        List<ArticleCountDO> articleCountDOS = articleDao.selectArticleCountByUserId(
                userId, startDate.format(YMD), endDate.format(YMD));

        Map<String, Long> dateCountMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(articleCountDOS)) {
            dateCountMap = articleCountDOS.stream()
                    .collect(Collectors.toMap(ArticleCountDO::getDate, ArticleCountDO::getCount, (a, b) -> a));
        }

        List<PublishHeatmapRspVO.PublishHeatmapItemVO> items = Lists.newArrayList();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            String key = d.format(YMD);
            long count = dateCountMap.getOrDefault(key, 0L);
            items.add(PublishHeatmapRspVO.PublishHeatmapItemVO.builder()
                    .date(key)
                    .count((int) count)
                    .build());
        }

        PublishHeatmapRspVO vo = PublishHeatmapRspVO.builder().items(items).build();
        return Response.success(vo);
    }

    @Override
    public Response<CategoryRatioRspVO> getCategoryRatio() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();

        QueryWrapper<ArticleDO> articleWrapper = new QueryWrapper<>();
        articleWrapper.select("id").lambda()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false);
        List<ArticleDO> userArticles = articleMapper.selectList(articleWrapper);
        if (CollectionUtils.isEmpty(userArticles)) {
            CategoryRatioRspVO empty = CategoryRatioRspVO.builder().items(Lists.newArrayList()).build();
            return Response.success(empty);
        }
        List<Long> articleIds = userArticles.stream().map(ArticleDO::getId).collect(Collectors.toList());

        QueryWrapper<ArticleCategoryRelDO> relWrapper = new QueryWrapper<>();
        relWrapper.select("category_id, COUNT(*) AS count")
                .lambda()
                .in(ArticleCategoryRelDO::getArticleId, articleIds)
                .groupBy(ArticleCategoryRelDO::getCategoryId);
        List<Map<String, Object>> categoryCounts = articleCategoryRelMapper.selectMaps(relWrapper);

        Map<Long, Long> categoryCountMap = Maps.newHashMap();
        long totalCount = 0L;
        if (!CollectionUtils.isEmpty(categoryCounts)) {
            for (Map<String, Object> row : categoryCounts) {
                Long categoryId = Long.valueOf(row.get("category_id").toString());
                Long cnt = Long.valueOf(row.get("count").toString());
                categoryCountMap.put(categoryId, cnt);
                totalCount += cnt;
            }
        }

        List<CategoryDO> categories = categoryDao.selectAllCategory(userId);
        Map<Long, String> categoryNameMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(categories)) {
            for (CategoryDO c : categories) {
                categoryNameMap.put(c.getId(), c.getName());
            }
        }

        List<CategoryRatioRspVO.RatioItemVO> items = Lists.newArrayList();
        for (Map.Entry<Long, Long> entry : categoryCountMap.entrySet()) {
            long cnt = entry.getValue();
            double percentage = totalCount > 0 ? (double) cnt / totalCount * 100.0 : 0.0;
            String name = categoryNameMap.getOrDefault(entry.getKey(), "\u672a\u77e5");
            items.add(CategoryRatioRspVO.RatioItemVO.builder()
                    .name(name)
                    .count(cnt)
                    .percentage(Math.round(percentage * 100.0) / 100.0)
                    .build());
        }
        items.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        CategoryRatioRspVO vo = CategoryRatioRspVO.builder().items(items).build();
        return Response.success(vo);
    }

    @Override
    public Response<TagRatioRspVO> getTagRatio() {
        UserDO u = getCurrentUser();
        Long userId = u.getId();

        QueryWrapper<ArticleDO> articleWrapper = new QueryWrapper<>();
        articleWrapper.select("id").lambda()
                .eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false);
        List<ArticleDO> userArticles = articleMapper.selectList(articleWrapper);
        if (CollectionUtils.isEmpty(userArticles)) {
            TagRatioRspVO empty = TagRatioRspVO.builder().items(Lists.newArrayList()).build();
            return Response.success(empty);
        }
        List<Long> articleIds = userArticles.stream().map(ArticleDO::getId).collect(Collectors.toList());

        QueryWrapper<ArticleTagRelDO> relWrapper = new QueryWrapper<>();
        relWrapper.select("tag_id, COUNT(*) AS count")
                .lambda()
                .in(ArticleTagRelDO::getArticleId, articleIds)
                .groupBy(ArticleTagRelDO::getTagId);
        List<Map<String, Object>> tagCounts = articleTagRelMapper.selectMaps(relWrapper);

        Map<Long, Long> tagCountMap = Maps.newHashMap();
        long totalCount = 0L;
        if (!CollectionUtils.isEmpty(tagCounts)) {
            for (Map<String, Object> row : tagCounts) {
                Long tagId = Long.valueOf(row.get("tag_id").toString());
                Long cnt = Long.valueOf(row.get("count").toString());
                tagCountMap.put(tagId, cnt);
                totalCount += cnt;
            }
        }

        List<TagDO> tags = tagDao.selectAllByUserId(userId);
        Map<Long, String> tagNameMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(tags)) {
            for (TagDO t : tags) {
                tagNameMap.put(t.getId(), t.getName());
            }
        }

        List<TagRatioRspVO.RatioItemVO> items = Lists.newArrayList();
        for (Map.Entry<Long, Long> entry : tagCountMap.entrySet()) {
            long cnt = entry.getValue();
            double percentage = totalCount > 0 ? (double) cnt / totalCount * 100.0 : 0.0;
            String name = tagNameMap.getOrDefault(entry.getKey(), "\u672a\u77e5");
            items.add(TagRatioRspVO.RatioItemVO.builder()
                    .name(name)
                    .count(cnt)
                    .percentage(Math.round(percentage * 100.0) / 100.0)
                    .build());
        }
        items.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        TagRatioRspVO vo = TagRatioRspVO.builder().items(items).build();
        return Response.success(vo);
    }

    private int resolvePeriodDays(String period) {
        if ("30d".equals(period)) {
            return 30;
        }
        if ("90d".equals(period)) {
            return 90;
        }
        return 7;
    }
}