package com.apiscan.domain;

import com.apiscan.common.enums.SubscriptionTier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Organization entity — multi-tenant workspace.
 */
@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
public class Organization {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionTier tier = SubscriptionTier.FREE;

    @Column(name = "stripe_customer_id", unique = true)
    private String stripeCustomerId;

    @Column(name = "monthly_quota", nullable = false)
    private int monthlyQuota = 50;

    @Column(name = "used_quota", nullable = false)
    private int usedQuota = 0;

    @Column(name = "quota_reset_at", nullable = false)
    private LocalDateTime quotaResetAt = LocalDateTime.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrgMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();
}
