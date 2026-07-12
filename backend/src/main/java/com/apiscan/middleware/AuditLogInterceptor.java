package com.apiscan.middleware;

import com.apiscan.domain.AuditLog;
import com.apiscan.repository.AuditLogRepository;
import com.apiscan.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Set;

/**
 * Audit log interceptor — records sensitive actions for compliance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogInterceptor implements HandlerInterceptor {

    private final AuditLogRepository auditLogRepository;

    private static final Set<String> AUDITABLE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        if (!AUDITABLE_METHODS.contains(request.getMethod()))
            return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal))
            return;
        if (principal.getOrgId() == null)
            return;

        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setOrgId(principal.getOrgId());
            auditLog.setUserId(principal.getId());
            auditLog.setAction(request.getMethod() + " " + request.getRequestURI());
            auditLog.setMetadata(Map.of(
                    "statusCode", response.getStatus(),
                    "method", request.getMethod(),
                    "path", request.getRequestURI()));
            auditLog.setIpAddress(getClientIp(request));
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
