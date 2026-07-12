package com.apiscan.repository;

import com.apiscan.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Optional<Subscription> findByOrgId(String orgId);

    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
