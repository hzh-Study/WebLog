package com.quanxiaoha.weblog.web.service;

import com.quanxiaoha.weblog.admin.dao.AdminUserDao;
import com.quanxiaoha.weblog.admin.dao.AdminUserRoleDao;
import com.quanxiaoha.weblog.admin.model.vo.user.QueryUserDetailRspVO;
import com.quanxiaoha.weblog.admin.model.vo.user.RegisterUserReqVO;
import com.quanxiaoha.weblog.admin.service.impl.AdminUserServiceImpl;
import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.domain.dos.UserDO;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import com.quanxiaoha.weblog.common.exception.BizException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

class AdminUserServiceImplTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerShouldInsertUserAndDefaultRole() {
        AdminUserServiceImpl service = new AdminUserServiceImpl();
        AdminUserDao userDao = mock(AdminUserDao.class);
        AdminUserRoleDao userRoleDao = mock(AdminUserRoleDao.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userDao.selectByUsername("alice")).thenReturn(null);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPwd");

        ReflectionTestUtils.setField(service, "userDao", userDao);
        ReflectionTestUtils.setField(service, "userRoleDao", userRoleDao);
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);

        Response response = service.register(RegisterUserReqVO.builder()
                .username("alice")
                .nickname("Alice")
                .password("secret123")
                .build());

        Assertions.assertTrue(response.isSuccess());
        verify(userDao).insert(ArgumentMatchers.argThat(user ->
                "alice".equals(user.getUsername())
                        && "Alice".equals(user.getNickname())
                        && "encodedPwd".equals(user.getPassword())));
        verify(userRoleDao).insert(ArgumentMatchers.argThat(role ->
                "alice".equals(role.getUsername()) && "ROLE_ADMIN".equals(role.getRole())));
    }

    @Test
    void queryCurrentUserDetailShouldReadAuthenticatedUser() {
        AdminUserServiceImpl service = new AdminUserServiceImpl();
        AdminUserDao userDao = mock(AdminUserDao.class);
        ReflectionTestUtils.setField(service, "userDao", userDao);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", null));

        when(userDao.selectByUsername("alice")).thenReturn(UserDO.builder()
                .id(3L)
                .username("alice")
                .nickname("Alice")
                .avatar("avatar.png")
                .bio("bio")
                .build());

        Response<QueryUserDetailRspVO> response = service.queryCurrentUserDetail();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("alice", response.getData().getUsername());
        Assertions.assertEquals("Alice", response.getData().getNickname());
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        AdminUserServiceImpl service = new AdminUserServiceImpl();
        AdminUserDao userDao = mock(AdminUserDao.class);
        ReflectionTestUtils.setField(service, "userDao", userDao);
        ReflectionTestUtils.setField(service, "userRoleDao", mock(AdminUserRoleDao.class));
        ReflectionTestUtils.setField(service, "passwordEncoder", mock(PasswordEncoder.class));

        when(userDao.selectByUsername("alice")).thenReturn(UserDO.builder().username("alice").build());

        BizException ex = Assertions.assertThrows(BizException.class, () -> service.register(RegisterUserReqVO.builder()
                .username("alice")
                .nickname("Alice")
                .password("secret123")
                .build()));

        Assertions.assertEquals(ResponseCodeEnum.USERNAME_ALREADY_EXISTS.getErrorCode(), ex.getErrorCode());
    }
}
