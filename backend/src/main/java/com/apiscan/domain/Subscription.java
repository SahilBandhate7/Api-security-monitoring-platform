package com.apiscan.domain;

import com.apiscan.common.enums.SubscriptionTier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Subscription entity — Stripe subscription record per organization.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "org_id", nullable = false, length = 36)
    private String orgId;

    @Column(name = "stripe_subscription_id", nullable = false, unique = true)
    private String stripeSubscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionTier tier;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "current_period_end", nullable = false)
    private LocalDateTime currentPeriodEnd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
