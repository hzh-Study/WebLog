package com.quanxiaoha.weblog.admin.service.impl;

import com.quanxiaoha.weblog.admin.dao.AdminArticleCategoryRelDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleContentDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleTagRelDao;
import com.quanxiaoha.weblog.admin.dao.AdminArticleVersionDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDetailRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDiffRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionListRspVO;
import com.quanxiaoha.weblog.admin.service.AdminArticleVersionService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.ArticleContentDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.ArticleVersionDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminArticleVersionServiceImpl implements AdminArticleVersionService {

    @Autowired
    private AdminArticleVersionDao adminArticleVersionDao;
    @Autowired
    private AdminArticleDao adminArticleDao;
    @Autowired
    private AdminArticleContentDao adminArticleContentDao;
    @Autowired
    private AdminArticleTagRelDao adminArticleTagRelDao;
    @Autowired
    private AdminArticleCategoryRelDao adminArticleCategoryRelDao;
    @Autowired
    private AdminUserDao adminUserDao;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public AdminArticleVersionServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Response<ArticleVersionListRspVO> listVersions(Long articleId) {
        getOwnArticle(articleId);
        List<ArticleVersionDO> versions = adminArticleVersionDao.selectListByArticleId(articleId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ArticleVersionListRspVO.VersionItemVO> items = versions.stream()
                .map(v -> ArticleVersionListRspVO.VersionItemVO.builder()
                        .versionId(v.getId())
                        .versionNum(v.getVersionNum())
                        .title(v.getTitle())
                        .changeSummary(v.getChangeSummary())
                        .createTime(v.getCreateTime() != null ? sdf.format(v.getCreateTime()) : null)
                        .build())
                .collect(Collectors.toList());
        ArticleVersionListRspVO rspVO = ArticleVersionListRspVO.builder().items(items).build();
        return Response.success(rspVO);
    }

    @Override
    public Response<ArticleVersionDetailRspVO> getVersionDetail(Long versionId) {
        ArticleVersionDO version = adminArticleVersionDao.selectById(versionId);
        if (version == null) {
            throw new BizException(ResponseCodeEnum.VERSION_NOT_FOUND);
        }
        getOwnArticle(version.getArticleId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArticleVersionDetailRspVO vo = ArticleVersionDetailRspVO.builder()
                .versionId(version.getId())
                .versionNum(version.getVersionNum())
                .title(version.getTitle())
                .content(version.getContent())
                .description(version.getDescription())
                .changeSummary(version.getChangeSummary())
                .createTime(version.getCreateTime() != null ? sdf.format(version.getCreateTime()) : null)
                .build();
        return Response.success(vo);
    }

    @Override
    public Response<ArticleVersionDiffRspVO> diffVersions(Long versionIdA, Long versionIdB) {
        ArticleVersionDO versionA = adminArticleVersionDao.selectById(versionIdA);
        ArticleVersionDO versionB = adminArticleVersionDao.selectById(versionIdB);
        if (versionA == null || versionB == null) {
            throw new BizException(ResponseCodeEnum.VERSION_NOT_FOUND);
        }
        getOwnArticle(versionA.getArticleId());
        getOwnArticle(versionB.getArticleId());
        ArticleVersionDiffRspVO vo = ArticleVersionDiffRspVO.builder()
                .titleA(versionA.getTitle())
                .titleB(versionB.getTitle())
                .contentA(versionA.getContent())
                .contentB(versionB.getContent())
                .build();
        return Response.success(vo);
    }

    @Override
    public Response rollbackVersion(Long versionId) {
        ArticleVersionDO targetVersion = adminArticleVersionDao.selectById(versionId);
        if (targetVersion == null) {
            throw new BizException(ResponseCodeEnum.VERSION_NOT_FOUND);
        }
        Long articleId = targetVersion.getArticleId();
        UserDO currentUser = getCurrentUser();
        ArticleDO articleDO = getOwnArticle(articleId);
        ArticleContentDO articleContentDO = adminArticleContentDao.queryByArticleId(articleId);

        boolean isExecuteSuccess = transactionTemplate.execute(status -> {
            Integer currentMaxVersionNum = adminArticleVersionDao.selectMaxVersionNum(articleId);
            Integer snapshotVersionNum = currentMaxVersionNum + 1;

            ArticleVersionDO snapshot = ArticleVersionDO.builder()
                    .articleId(articleId)
                    .userId(currentUser.getId())
                    .title(articleDO.getTitle())
                    .content(articleContentDO != null ? articleContentDO.getContent() : "")
                    .description(articleDO.getDescription())
                    .changeSummary("回滚至版本 " + targetVersion.getVersionNum())
                    .versionNum(snapshotVersionNum)
                    .createTime(new Date())
                    .build();
            adminArticleVersionDao.insert(snapshot);

            ArticleDO updatedArticle = ArticleDO.builder()
                    .id(articleId)
                    .title(targetVersion.getTitle())
                    .description(targetVersion.getDescription())
                    .updateTime(new Date())
                    .build();
            adminArticleDao.updateById(updatedArticle);

            ArticleContentDO updatedContent = ArticleContentDO.builder()
                    .articleId(articleId)
                    .content(targetVersion.getContent())
                    .build();
            int contentRows = adminArticleContentDao.updateByArticleId(updatedContent);
            if (contentRows == 0) {
                adminArticleContentDao.insertArticleContent(updatedContent);
            }

            adminArticleVersionDao.deleteOldestExceed(articleId, 20);
            return true;
        });

        if (!isExecuteSuccess) {
            throw new BizException(ResponseCodeEnum.VERSION_ROLLBACK_FAILED);
        }
        return Response.success();
    }

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = adminUserDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }

    private ArticleDO getOwnArticle(Long articleId) {
        ArticleDO articleDO = adminArticleDao.queryByArticleId(articleId, getCurrentUser().getId());
        if (articleDO == null) {
            throw new BizException(ResponseCodeEnum.ARTICLE_ACCESS_DENIED);
        }
        return articleDO;
    }
}