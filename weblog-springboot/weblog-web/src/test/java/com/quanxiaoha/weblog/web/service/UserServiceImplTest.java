package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.domain.mapper.ArticleMapper;
import com.quanxiaoha.weblog.common.domain.mapper.UserMapper;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserItemRspVO;
import com.quanxiaoha.weblog.web.model.vo.user.QueryPublicUserDetailReqVO;
import com.quanxiaoha.weblog.web.model.vo.user.SearchPublicUserReqVO;
import com.quanxiaoha.weblog.web.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Test
    void queryPublicUserDetailShouldReturnProfileAndCount() {
        UserServiceImpl service = new UserServiceImpl();
        UserMapper userMapper = mock(UserMapper.class);
        ArticleMapper articleMapper = mock(ArticleMapper.class);
        ReflectionTestUtils.setField(service, "userMapper", userMapper);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);

        when(userMapper.selectOne(any())).thenReturn(UserDO.builder()
                .id(1L)
                .username("alice")
                .nickname("Alice")
                .bio("hello")
                .build());
        when(articleMapper.selectCount(any())).thenReturn(2L);

        Response<QueryPublicUserDetailRspVO> response = service.queryPublicUserDetail(QueryPublicUserDetailReqVO.builder().username("alice").build());

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("alice", response.getData().getUsername());
        Assertions.assertEquals(2L, response.getData().getPublicArticleCount());
    }

    @Test
    void searchPublicUsersShouldFilterUsersWithoutPublicArticles() {
        UserServiceImpl service = new UserServiceImpl();
        UserMapper userMapper = mock(UserMapper.class);
        ArticleMapper articleMapper = mock(ArticleMapper.class);
        ReflectionTestUtils.setField(service, "userMapper", userMapper);
        ReflectionTestUtils.setField(service, "articleMapper", articleMapper);

        when(userMapper.selectList(any())).thenReturn(Arrays.asList(
                UserDO.builder().id(1L).username("alice").nickname("Alice").build(),
                UserDO.builder().id(2L).username("bob").nickname("Bob").build()
        ));
        Map<String, Object> aliceArticleCount = new HashMap<>();
        aliceArticleCount.put("userId", 1L);
        aliceArticleCount.put("articleCount", 3L);
        when(articleMapper.selectMaps(any())).thenReturn(Collections.singletonList(aliceArticleCount));

        Response<java.util.List<SearchPublicUserItemRspVO>> response = service.searchPublicUsers(SearchPublicUserReqVO.builder().keyword("a").build());

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals("alice", response.getData().get(0).getUsername());
    }

    @Test
    void searchPublicUsersShouldReturnEmptyWhenKeywordBlank() {
        UserServiceImpl service = new UserServiceImpl();
        ReflectionTestUtils.setField(service, "userMapper", mock(UserMapper.class));
        ReflectionTestUtils.setField(service, "articleMapper", mock(ArticleMapper.class));

        Response<java.util.List<SearchPublicUserItemRspVO>> response = service.searchPublicUsers(SearchPublicUserReqVO.builder().keyword(" ").build());

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(Collections.emptyList(), response.getData());
    }
}
