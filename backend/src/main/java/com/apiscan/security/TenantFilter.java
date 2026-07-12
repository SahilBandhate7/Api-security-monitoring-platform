package com.apiscan.security;

import com.apiscan.common.enums.UserRole;
import com.apiscan.domain.OrgMember;
import com.apiscan.repository.OrgMemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tenant filter — extracts orgId from URL path and resolves the user's role
 * within that organization. Enriches the SecurityContext with org-scoped
 * principal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private static final Pattern ORG_PATTERN = Pattern.compile("/api/orgs/([a-zA-Z0-9-]+)");

    private final OrgMemberRepository orgMemberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            String orgId = extractOrgId(request.getRequestURI());

            if (orgId != null) {
                OrgMember member = orgMemberRepository
                        .findByUserIdAndOrganizationId(principal.getId(), orgId)
                        .orElse(null);

                if (member != null) {
                    UserPrincipal enriched = new UserPrincipal(
                            principal.getId(), principal.getEmail(), principal.getPassword(),
                            orgId, member.getRole());

                    UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(enriched,
                            null, enriched.getAuthorities());
                    newAuth.setDetails(auth.getDetails());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    request.setAttribute("orgId", orgId);
                    request.setAttribute("userRole", member.getRole());
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\":\"Not a member of this organization\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractOrgId(String uri) {
        Matcher matcher = ORG_PATTERN.matcher(uri);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/billing/webhook") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/actuator/health");
    }
}
