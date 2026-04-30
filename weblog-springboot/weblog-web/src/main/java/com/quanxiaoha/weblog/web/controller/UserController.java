package com.quanxiaoha.weblog.web.controller;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailReqVO;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserReqVO;
import com.quanxiaoha.weblog.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/public")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/detail")
    @ApiOperationLog(description = "获取用户公开主页详情")
    public Response<QueryPublicUserDetailRspVO> queryPublicUserDetail(@RequestBody @Validated QueryPublicUserDetailReqVO queryPublicUserDetailReqVO) {
        return userService.queryPublicUserDetail(queryPublicUserDetailReqVO);
    }

    @PostMapping("/search")
    @ApiOperationLog(description = "搜索公开用户")
    public Response<List<SearchPublicUserItemRspVO>> searchPublicUsers(@RequestBody(required = false) SearchPublicUserReqVO searchPublicUserReqVO) {
        return userService.searchPublicUsers(searchPublicUserReqVO == null ? new SearchPublicUserReqVO() : searchPublicUserReqVO);
    }
}
