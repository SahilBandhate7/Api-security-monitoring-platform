package com.apiscan.config;

import com.apiscan.middleware.AuditLogInterceptor;
import com.apiscan.middleware.QuotaGuardInterceptor;
import com.apiscan.middleware.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers middleware interceptors with Spring MVC.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    private final QuotaGuardInterceptor quotaGuardInterceptor;
    private final AuditLogInterceptor auditLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/billing/webhook", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");

        registry.addInterceptor(quotaGuardInterceptor)
                .addPathPatterns("/api/orgs/*/scans");

        registry.addInterceptor(auditLogInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/billing/webhook");
    }
}
