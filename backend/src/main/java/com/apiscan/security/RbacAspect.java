package com.apiscan.security;

import com.apiscan.common.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AOP aspect for enforcing @RequiresRole annotations.
 * Checks the authenticated user's role against the required roles.
 */
@Slf4j
@Aspect
@Component
public class RbacAspect {

    @Around("@annotation(requiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Authentication required");
        }

        if (principal.getRole() == null) {
            throw new AccessDeniedException("Organization context required");
        }

        Set<UserRole> allowedRoles = Arrays.stream(requiresRole.value()).collect(Collectors.toSet());

        // OWNER has all permissions — always allow
        if (principal.getRole() == UserRole.OWNER || allowedRoles.contains(principal.getRole())) {
            return joinPoint.proceed();
        }

        log.warn("Access denied for user {} with role {} on {}",
                principal.getId(), principal.getRole(), joinPoint.getSignature().getName());
        throw new AccessDeniedException(
                "Insufficient permissions. Required role: " + allowedRoles);
    }
}
