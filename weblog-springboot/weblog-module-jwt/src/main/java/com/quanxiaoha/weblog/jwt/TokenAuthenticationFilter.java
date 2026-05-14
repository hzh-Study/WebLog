package com.quanxiaoha.weblog.jwt;

import com.quanxiaoha.weblog.common.Response;
import com.quanxiaoha.weblog.common.enums.ResponseCodeEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-04-17 16:22
 * @description: Token 校验过滤器
 **/
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(tokenHeaderKey);
        if (StringUtils.startsWith(header, String.format("%s ", tokenPrefix))) {
            String token = StringUtils.substring(header, 7);
            log.debug("token: {}", token);
            if (StringUtils.isNotBlank(token) && jwtTokenHelper.validateToken(token)) {
                String username = jwtTokenHelper.getUsernameByToken(token);

                if (StringUtils.isNotBlank(username)) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (Exception e) {
                        log.warn("Token 对应的用户不存在或已失效: {}", username);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
