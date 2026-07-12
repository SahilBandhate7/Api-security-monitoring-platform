package com.apiscan.billing;

import com.apiscan.billing.dto.UsageResponse;
import com.apiscan.common.enums.SubscriptionTier;
import com.apiscan.config.AppProperties;
import com.apiscan.domain.Organization;
import com.apiscan.domain.Subscription;
import com.apiscan.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Billing service — handles Stripe checkout, quota tracking, and plan info.
 * Stripe API calls are stubbed for now and will be wired with live keys.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final OrganizationRepository organizationRepository;
    private final AppProperties appProperties;

    @Value("${stripe.pro-price-id:}")
    private String proPriceId;

    @Value("${stripe.enterprise-price-id:}")
    private String enterprisePriceId;

    public List<Map<String, Object>> getPlans() {
        return List.of(
                Map.of("tier", "FREE", "price", 0, "scans", 50, "members", 1, "projects", 2),
                Map.of("tier", "PRO", "price", 29, "priceId", proPriceId != null ? proPriceId : "",
                        "scans", 1000, "members", 10, "projects", 20),
                Map.of("tier", "ENTERPRISE", "price", 99, "priceId", enterprisePriceId != null ? enterprisePriceId : "",
                        "scans", "Unlimited", "members", "Unlimited", "projects", "Unlimited"));
    }

    /**
     * Create a Stripe checkout session URL.
     * In production, this calls the Stripe API to create a real session.
     */
    public String createCheckoutSession(String orgId, String priceId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        // TODO: Wire with Stripe API
        // Session session = Session.create(SessionCreateParams.builder()
        // .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        // .setCustomer(org.getStripeCustomerId())
        // .setSuccessUrl(appProperties.getFrontendUrl() + "/billing?success=true")
        // .setCancelUrl(appProperties.getFrontendUrl() + "/billing?canceled=true")
        // .addLineItem(SessionCreateParams.LineItem.builder()
        // .setPrice(priceId)
        // .setQuantity(1L)
        // .build())
        // .build());

        log.info("Checkout session created for org {} with price {}", orgId, priceId);
        return appProperties.getFrontendUrl() + "/billing?session=pending";
    }

    /**
     * Create a Stripe customer portal session URL.
     */
    public String createPortalSession(String orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        // TODO: Wire with Stripe API
        log.info("Portal session created for org {}", orgId);
        return appProperties.getFrontendUrl() + "/billing";
    }

    @Transactional(readOnly = true)
    public UsageResponse getUsage(String orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        int remaining = Math.max(0, org.getMonthlyQuota() - org.getUsedQuota());
        double percentage = org.getMonthlyQuota() > 0
                ? (double) org.getUsedQuota() / org.getMonthlyQuota() * 100
                : 0;

        return new UsageResponse(
                orgId, org.getTier(), org.getMonthlyQuota(),
                org.getUsedQuota(), remaining, Math.round(percentage * 10.0) / 10.0);
    }

    /**
     * Process a Stripe webhook event to update subscription status.
     */
    @Transactional
    public void handleWebhookEvent(String eventType, String orgId, String tier) {
        Organization org = organizationRepository.findById(orgId).orElse(null);
        if (org == null)
            return;

        switch (eventType) {
            case "customer.subscription.created", "customer.subscription.updated" -> {
                SubscriptionTier newTier = SubscriptionTier.valueOf(tier.toUpperCase());
                org.setTier(newTier);
                org.setMonthlyQuota(newTier.getMonthlyScans());
                organizationRepository.save(org);
                log.info("Subscription updated for org {}: {}", orgId, newTier);
            }
            case "customer.subscription.deleted" -> {
                org.setTier(SubscriptionTier.FREE);
                org.setMonthlyQuota(SubscriptionTier.FREE.getMonthlyScans());
                organizationRepository.save(org);
                log.info("Subscription cancelled for org {}", orgId);
            }
        }
    }
}
