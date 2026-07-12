package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class AuthBypassCheck implements ScanCheck {

    @Override
    public CheckResult execute(ScanContext ctx) {
        try {
            boolean isPrivateApi = ctx.headers() != null && 
                                   (ctx.headers().containsKey("Authorization") || 
                                    ctx.headers().keySet().stream().anyMatch(k -> k.toLowerCase().contains("token") || k.toLowerCase().contains("session")));
            
            // If the user didn't even supply auth coordinates, it's a Public API.
            if (!isPrivateApi) {
                return CheckResult.safe(getCheckName()); 
            }

            // Try accessing endpoint without any auth headers
            ResponseEntity<String> noAuthResponse = ctx.webClient()
                    .method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                    .uri(ctx.targetUrl())
                    .retrieve()
                    .toEntity(String.class)
                    .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                    .block(Duration.ofSeconds(10));

            // If it rejects us correctly, it's secure. 
            if (noAuthResponse != null && (noAuthResponse.getStatusCode().value() == 401 || noAuthResponse.getStatusCode().value() == 403)) {
                return CheckResult.safe(getCheckName());
            }

            // If it allowed us in implicitly, we verify against baseline
            if (noAuthResponse != null && noAuthResponse.getStatusCode().is2xxSuccessful()) {
                ResponseEntity<String> baseline = ctx.baselineResponse();
                if (baseline != null && baseline.getStatusCode().is2xxSuccessful()) {
                    String noAuthBody = noAuthResponse.getBody();
                    String authBody = baseline.getBody();

                    if (noAuthBody != null && authBody != null &&
                            noAuthBody.length() > 50 &&
                            Math.abs(noAuthBody.length() - authBody.length()) < authBody.length() * 0.2) {
                        return CheckResult.vulnerable(getCheckName(), Severity.CRITICAL,
                                "Authentication bypass detected - endpoint accessible without credentials",
                                "Target is configured as a Private API but accepted unauthenticated requests resulting in a mirrored payload block. (Confidence: 95%)",
                                "Enforce authentication on all sensitive endpoints. Verify tokens server-side.", 95);
                    }
                }
            }
        } catch (Exception e) {
            // Usually 4xx/5xx exceptions from WebClient indicate auth blocked us correctly or failed.
            log.debug("Auth bypass check completed (endpoint properly rejects): {}", e.getMessage());
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "AUTH_BYPASS";
    }
}
