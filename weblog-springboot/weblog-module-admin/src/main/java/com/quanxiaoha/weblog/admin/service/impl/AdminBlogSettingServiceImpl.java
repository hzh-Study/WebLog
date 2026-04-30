package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.admin.model.vo.blogsetting.QueryBlogSettingRspVO;
import com.quanxiaoha.weblog.admin.model.vo.blogsetting.UpdateBlogSettingReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.QueryUserDetailRspVO;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.service.AdminBlogSettingService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.mapper.BlogSettingMapper;
import com.quanxiaoha.weblog.common.domain.dos.BlogSettingDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminBlogSettingServiceImpl extends ServiceImpl<BlogSettingMapper, BlogSettingDO> implements AdminBlogSettingService {
    @Autowired
    private AdminUserDao userDao;

    @Override
    public Response updateBlogSetting(UpdateBlogSettingReqVO updateBlogSettingReqVO) {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = UserDO.builder()
                .nickname(updateBlogSettingReqVO.getNickname())
                .avatar(updateBlogSettingReqVO.getAvatar())
                .bio(updateBlogSettingReqVO.getBio())
                .website(updateBlogSettingReqVO.getWebsite())
                .githubHome(updateBlogSettingReqVO.getGithubHome())
                .giteeHome(updateBlogSettingReqVO.getGiteeHome())
                .csdnHome(updateBlogSettingReqVO.getCsdnHome())
                .zhihuHome(updateBlogSettingReqVO.getZhihuHome())
                .updateTime(new Date())
                .build();
        userDao.updateByUsername(username, userDO);
        return Response.success();
    }

    @Override
    public Response queryBlogSettingDetail() {
        UserDO userDO = getCurrentUser();
        return Response.success(QueryBlogSettingRspVO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .bio(userDO.getBio())
                .website(userDO.getWebsite())
                .githubHome(userDO.getGithubHome())
                .giteeHome(userDO.getGiteeHome())
                .csdnHome(userDO.getCsdnHome())
                .zhihuHome(userDO.getZhihuHome())
                .build());
    }

    @Override
    public Response<QueryUserDetailRspVO> queryNicknameAndAvatar() {
        UserDO userDO = getCurrentUser();
        return Response.success(QueryUserDetailRspVO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .bio(userDO.getBio())
                .website(userDO.getWebsite())
                .githubHome(userDO.getGithubHome())
                .giteeHome(userDO.getGiteeHome())
                .csdnHome(userDO.getCsdnHome())
                .zhihuHome(userDO.getZhihuHome())
                .build());
    }

    private UserDO getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserDO userDO = userDao.selectByUsername(username);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        return userDO;
    }
}
