package com.quanxiaoha.weblog.admin.service;

import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDetailRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionDiffRspVO;
import com.quanxiaoha.weblog.admin.model.vo.article.ArticleVersionListRspVO;
import com.quanxiaoha.weblog.common.Response;

public interface AdminArticleVersionService {

    Response<ArticleVersionListRspVO> listVersions(Long articleId);

    Response<ArticleVersionDetailRspVO> getVersionDetail(Long versionId);

    Response<ArticleVersionDiffRspVO> diffVersions(Long versionIdA, Long versionIdB);

    Response rollbackVersion(Long versionId);
}