package com.apiscan.billing.dto;

import com.apiscan.common.enums.SubscriptionTier;

public record UsageResponse(
        String orgId,
        SubscriptionTier tier,
        int monthlyQuota,
        int usedQuota,
        int remainingQuota,
        double usagePercentage) {
}
