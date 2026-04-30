package com.quanxiaoha.weblog.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.constant.ArticleVisibilityConstants;
import com.quanxiaoha.weblog.common.domain.dos.ArticleDO;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailReqVO;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserReqVO;
import com.quanxiaoha.weblog.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response<QueryPublicUserDetailRspVO> queryPublicUserDetail(QueryPublicUserDetailReqVO queryPublicUserDetailReqVO) {
        UserDO userDO = findByUsername(queryPublicUserDetailReqVO.getUsername().trim());
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        return Response.success(QueryPublicUserDetailRspVO.builder()
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
                .publicArticleCount(countPublicArticles(userDO.getId()))
                .build());
    }

    @Override
    public Response<List<SearchPublicUserItemRspVO>> searchPublicUsers(SearchPublicUserReqVO searchPublicUserReqVO) {
        String keyword = searchPublicUserReqVO == null ? null : searchPublicUserReqVO.getKeyword();
        if (!StringUtils.hasText(keyword)) {
            return Response.success(Collections.emptyList());
        }

        String k = keyword.trim();
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getIsDeleted, false)
                .and(w -> w.like(UserDO::getUsername, k).or().like(UserDO::getNickname, k))
                .orderByAsc(UserDO::getUsername)
                .last("limit 8");

        List<UserDO> users = userMapper.selectList(wrapper);
        if (users.isEmpty()) {
            return Response.success(Collections.emptyList());
        }

        List<Long> userIds = users.stream().map(UserDO::getId).collect(Collectors.toList());
        Map<Long, Long> publicArticleCountMap = countPublicArticlesByUserIds(userIds);

        List<SearchPublicUserItemRspVO> list = users.stream()
                .map(userDO -> SearchPublicUserItemRspVO.builder()
                        .id(userDO.getId())
                        .username(userDO.getUsername())
                        .nickname(userDO.getNickname())
                        .avatar(userDO.getAvatar())
                        .bio(userDO.getBio())
                        .publicArticleCount(publicArticleCountMap.getOrDefault(userDO.getId(), 0L))
                        .build())
                .filter(item -> item.getPublicArticleCount() > 0)
                .collect(Collectors.toList());

        return Response.success(list);
    }

    private UserDO findByUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username).eq(UserDO::getIsDeleted, false).last("limit 1");
        return userMapper.selectOne(wrapper);
    }

    private Long countPublicArticles(Long userId) {
        LambdaQueryWrapper<ArticleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getIsDeleted, false)
                .eq(ArticleDO::getVisibility, ArticleVisibilityConstants.PUBLIC);
        return articleMapper.selectCount(wrapper);
    }

    private Map<Long, Long> countPublicArticlesByUserIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QueryWrapper<ArticleDO> wrapper = new QueryWrapper<>();
        wrapper.select("user_id AS userId", "COUNT(*) AS articleCount")
                .in("user_id", userIds)
                .eq("is_deleted", false)
                .eq("visibility", ArticleVisibilityConstants.PUBLIC)
                .groupBy("user_id");

        return articleMapper.selectMaps(wrapper).stream()
                .collect(Collectors.toMap(
                        row -> toLong(getMapValue(row, "userId", "user_id")),
                        row -> toLong(getMapValue(row, "articleCount", "article_count", "COUNT(*)")),
                        (a, b) -> a
                ));
    }

    private Object getMapValue(Map<String, Object> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key)) {
                return row.get(key);
            }
        }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return value == null ? 0L : Long.parseLong(String.valueOf(value));
    }
}
