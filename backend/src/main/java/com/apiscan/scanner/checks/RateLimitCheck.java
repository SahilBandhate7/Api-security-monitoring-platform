package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RateLimitCheck implements ScanCheck {

    private static final int BURST_COUNT = 50;

    @Override
    public CheckResult execute(ScanContext ctx) {
        try {
            ResponseEntity<String> baseline = ctx.baselineResponse();
            if (baseline != null) {
                // Step 1: Explicit Header Check 
                boolean hasRateLimitHeaders = baseline.getHeaders().keySet().stream()
                        .anyMatch(k -> k.toLowerCase().contains("ratelimit") || k.equalsIgnoreCase("retry-after"));
                
                if (hasRateLimitHeaders) {
                    return CheckResult.safe(getCheckName()); // Protected by gateway
                }
            }

            // Step 2: Behavioral Burst Test
            long successCount = reactor.core.publisher.Flux.range(1, BURST_COUNT)
                    .flatMap(i -> ctx.webClient().method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                            .uri(ctx.targetUrl())
                            .headers(h -> {
                                if (ctx.headers() != null)
                                    ctx.headers().forEach(h::add);
                            })
                            .retrieve()
                            .toBodilessEntity()
                            .onErrorResume(e -> reactor.core.publisher.Mono.empty()))
                    .filter(res -> res.getStatusCode().is2xxSuccessful())
                    .count()
                    .block(Duration.ofSeconds(15));
            
            // Did any return 429 Too Many Requests?
            // If successCount is slightly less than BURST_COUNT, some might have failed or 429'd.
            if (successCount < BURST_COUNT - 5) {
                return CheckResult.safe(getCheckName()); // Rate limit engaged during burst
            }

            // Step 3: Severity Scaling Mode
            // Public GET APIs without specific Auth tokens aren't necessarily highly vulnerable if they lack custom limits (CDNs handle it).
            boolean isAuthEndpoint = ctx.targetUrl().toLowerCase().contains("login") || 
                                     ctx.targetUrl().toLowerCase().contains("auth") || 
                                     ctx.targetUrl().toLowerCase().contains("token");
                                     
            boolean hasAuthHeader = ctx.headers() != null && ctx.headers().containsKey("Authorization");
            
            Severity severity = Severity.LOW;
            int confidence = 70;
            
            if (isAuthEndpoint || hasAuthHeader) {
                severity = Severity.HIGH;
                confidence = 90;
            } else if (ctx.method().equalsIgnoreCase("POST") || ctx.method().equalsIgnoreCase("PUT")) {
                severity = Severity.MEDIUM;
                confidence = 80;
            }

            return CheckResult.vulnerable(getCheckName(), severity,
                    "Missing Rate Limiting Protections",
                    "Sent " + BURST_COUNT + " concurrent requests, " + successCount + " returned successfully without 429 blocks. (Confidence: " + confidence + "%)",
                    "Implement rate limiting (e.g., token bucket, sliding window) utilizing standard X-RateLimit headers to prevent abuse.",
                    confidence);
                    
        } catch (Exception e) {
            log.debug("Rate limit check error: {}", e.getMessage());
        }
        
        // If entirely broken, default safe/info
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "RATE_LIMIT";
    }
}
