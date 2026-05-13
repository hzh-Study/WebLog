package com.quanxiaoha.weblog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserRoleDao;
import com.quanxiaoha.weblog.admin.model.vo.user.QueryUserDetailRspVO;
import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.model.vo.user.UpdateAdminPasswordReqVO;
import com.quanxiaoha.weblog.admin.service.AdminUserService;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.dos.UserRoleDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 12:08
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements AdminUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AdminUserDao userDao;
    @Autowired
    private AdminUserRoleDao userRoleDao;

    @Override
    public Response register(RegisterUserReqVO registerUserReqVO) {
        String username = registerUserReqVO.getUsername().trim();
        if (userDao.selectByUsername(username) != null) {
            throw new BizException(ResponseCodeEnum.USERNAME_ALREADY_EXISTS);
        }

        Date now = new Date();
        UserDO userDO = UserDO.builder()
                .username(username)
                .password(passwordEncoder.encode(registerUserReqVO.getPassword().trim()))
                .nickname(registerUserReqVO.getNickname().trim())
                .bio("这个用户还没有填写个人简介。")
                .createTime(now)
                .updateTime(now)
                .isDeleted(false)
                .build();
        userDao.insert(userDO);

        userRoleDao.insert(UserRoleDO.builder()
                .username(username)
                .role("ROLE_ADMIN")
                .createTime(now)
                .build());

        return Response.success();
    }

    @Override
    public Response updateAdminPassword(UpdateAdminPasswordReqVO updateAdminPasswordReqVO) {
        String username = SecurityUtils.getCurrentUsername();
        UserDO currentUser = userDao.selectByUsername(username);
        if (currentUser == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(updateAdminPasswordReqVO.getOldPassword(), currentUser.getPassword())) {
            throw new BizException(ResponseCodeEnum.PASSWORD_ERROR);
        }
        if (updateAdminPasswordReqVO.getOldPassword().equals(updateAdminPasswordReqVO.getNewPassword())) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR);
        }
        UserDO userDO = UserDO.builder()
                .password(passwordEncoder.encode(updateAdminPasswordReqVO.getNewPassword()))
                .updateTime(new Date())
                .build();
        userDao.updateByUsername(username, userDO);
        return Response.success();
    }

    @Override
    public Response<QueryUserDetailRspVO> queryCurrentUserDetail() {
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
