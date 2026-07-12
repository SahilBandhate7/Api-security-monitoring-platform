package com.apiscan.middleware;

import com.apiscan.domain.Organization;
import com.apiscan.repository.OrganizationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Quota guard — blocks scan requests when organization has exceeded monthly
 * quota.
 */
@Component
@RequiredArgsConstructor
public class QuotaGuardInterceptor implements HandlerInterceptor {

    private final OrganizationRepository orgRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws IOException {
        // Only applies to scan trigger endpoint (POST /api/orgs/{orgId}/scans)
        if (!request.getRequestURI().matches(".*/api/orgs/.*/scans$") ||
                !request.getMethod().equals("POST")) {
            return true;
        }

        String orgId = (String) request.getAttribute("orgId");
        if (orgId == null)
            return true;

        Organization org = orgRepository.findById(orgId).orElse(null);
        if (org == null)
            return true;

        if (org.getUsedQuota() >= org.getMonthlyQuota()) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Monthly scan quota exceeded. Upgrade your plan.\"}");
            return false;
        }

        return true;
    }
}
