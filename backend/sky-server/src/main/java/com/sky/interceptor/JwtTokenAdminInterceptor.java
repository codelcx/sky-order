package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 令牌校验拦截器（管理端）
 */
@Component
@RequiredArgsConstructor
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    /**
     * 前置拦截：校验令牌
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 从请求头获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        // 令牌为空，返回 401
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error("NOT_LOGIN")));
            return false;
        }

        // 解析令牌，提取员工 ID 存入 ThreadLocal
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = claims.get(JwtClaimsConstant.EMP_ID, Long.class);
            BaseContext.setCurrentId(empId);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error("TOKEN_INVALID")));
            return false;
        }
    }

    /**
     * 完成后清理 ThreadLocal
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        BaseContext.remove();
    }
}
