package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailReqVO;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserReqVO;

import java.util.List;

public interface UserService {
    Response<QueryPublicUserDetailRspVO> queryPublicUserDetail(QueryPublicUserDetailReqVO queryPublicUserDetailReqVO);

    Response<List<SearchPublicUserItemRspVO>> searchPublicUsers(SearchPublicUserReqVO searchPublicUserReqVO);
}
