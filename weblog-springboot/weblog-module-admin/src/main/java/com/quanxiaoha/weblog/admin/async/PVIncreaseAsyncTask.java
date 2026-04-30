package com.quanxiaoha.weblog.admin.async;

import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminStatisticsArticlePVDao;
import com.quanxiaoha.weblog.admin.dao.AdminStatisticsUserPVDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-07-02 9:31
 * @description: TODO
 **/
@Service
@Slf4j
public class PVIncreaseAsyncTask {

    @Autowired
    private AdminArticleDao articleDao;
    @Autowired
    private AdminStatisticsArticlePVDao statisticsArticlePVDao;
    @Autowired
    private AdminStatisticsUserPVDao statisticsUserPVDao;

    @Async
    public void handle(Long articleId) {
        log.info("## 文章被阅读量异步 +1, articleId: {}", articleId);
        articleDao.readNumIncrease(articleId);
        Long userId = articleDao.selectUserIdByArticleId(articleId);
        Date currDate = new Date();
        if (userId != null) {
            log.info("## 按用户日 PV +1, userId: {}, date: {}", userId, currDate);
            statisticsUserPVDao.pvIncrease(userId, currDate);
        }
        log.info("## 全站日 PV 异步 +1, currDate: {}", currDate);
        statisticsArticlePVDao.pvIncrease(currDate);
    }
}
