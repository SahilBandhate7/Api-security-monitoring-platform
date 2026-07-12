package com.apiscan.common.enums;

/**
 * Organization subscription tiers with associated quotas.
 */
public enum SubscriptionTier {
    FREE(50, 1, 2),
    PRO(1000, 10, 20),
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int monthlyScans;
    private final int maxMembers;
    private final int maxProjects;

    SubscriptionTier(int monthlyScans, int maxMembers, int maxProjects) {
        this.monthlyScans = monthlyScans;
        this.maxMembers = maxMembers;
        this.maxProjects = maxProjects;
    }

    public int getMonthlyScans() {
        return monthlyScans;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getMaxProjects() {
        return maxProjects;
    }
}
