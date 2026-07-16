package com.shanwei.user.config;

import com.shanwei.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            Claims claims = JwtUtil.parse(token);
            String userId = claims.getSubject();
            String saved = redisTemplate.opsForValue().get("login:" + userId);
            if (token.equals(saved)) {
                request.setAttribute("userId", Long.valueOf(userId));
                return true;
            }
        } catch (Exception ignored) {
        }
        response.setStatus(401);
        return false;
    }
}
