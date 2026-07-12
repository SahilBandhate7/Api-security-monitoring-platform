package com.apiscan.organization.dto;

import com.apiscan.common.enums.SubscriptionTier;

import java.time.LocalDateTime;
import java.util.List;

public record OrgResponse(
        String id,
        String name,
        String slug,
        SubscriptionTier tier,
        int monthlyQuota,
        int usedQuota,
        LocalDateTime createdAt,
        List<MemberInfo> members) {
    public record MemberInfo(
            String userId,
            String email,
            String name,
            String role,
            LocalDateTime invitedAt) {
    }
}
