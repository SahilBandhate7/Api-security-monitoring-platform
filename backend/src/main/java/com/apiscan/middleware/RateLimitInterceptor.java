package com.apiscan.middleware;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * Redis-backed rate limiter using token bucket algorithm.
 * Limits requests per IP to prevent abuse on public endpoints.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws IOException {
        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, WINDOW);
            }

            if (count != null && count > MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\":\"Too many requests. Please try again later.\"}");
                return false;
            }
        } catch (Exception e) {
            log.warn("Rate limit check failed (allowing request): {}", e.getMessage());
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
