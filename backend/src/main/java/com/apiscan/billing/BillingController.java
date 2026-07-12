package com.apiscan.billing;

import com.apiscan.billing.dto.CheckoutRequest;
import com.apiscan.billing.dto.UsageResponse;
import com.apiscan.common.ApiResponse;
import com.apiscan.common.enums.UserRole;
import com.apiscan.security.RequiresRole;
import com.apiscan.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(billingService.getPlans()));
    }

    @PostMapping("/checkout")
    @RequiresRole({ UserRole.OWNER })
    public ResponseEntity<ApiResponse<Map<String, String>>> createCheckout(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CheckoutRequest request) {
        String url = billingService.createCheckoutSession(principal.getOrgId(), request.priceId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url)));
    }

    @PostMapping("/portal")
    @RequiresRole({ UserRole.OWNER })
    public ResponseEntity<ApiResponse<Map<String, String>>> createPortal(
            @AuthenticationPrincipal UserPrincipal principal) {
        String url = billingService.createPortalSession(principal.getOrgId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url)));
    }

    @GetMapping("/usage")
    public ResponseEntity<ApiResponse<UsageResponse>> getUsage(
            @AuthenticationPrincipal UserPrincipal principal) {
        UsageResponse usage = billingService.getUsage(principal.getOrgId());
        return ResponseEntity.ok(ApiResponse.success(usage));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        // TODO: Verify Stripe signature and parse event
        // For now, acknowledge receipt
        return ResponseEntity.ok("Received");
    }
}
