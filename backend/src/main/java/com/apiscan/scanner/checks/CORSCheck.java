package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CORSCheck implements ScanCheck {

    @Override
    public CheckResult execute(ScanContext ctx) {
        try {
            ResponseEntity<String> response = ctx.webClient().method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                    .uri(ctx.targetUrl())
                    .header("Origin", "https://evil-attacker.com")
                    .headers(h -> {
                        if (ctx.headers() != null)
                            ctx.headers().forEach(h::add);
                    })
                    .retrieve()
                    .toEntity(String.class)
                    .block(Duration.ofSeconds(10));

            if (response != null) {
                String acao = response.getHeaders().getFirst("Access-Control-Allow-Origin");
                String acac = response.getHeaders().getFirst("Access-Control-Allow-Credentials");
                List<String> issues = new ArrayList<>();
                int confidence = 0;

                if ("*".equals(acao)) {
                    issues.add("Wildcard (*) Access-Control-Allow-Origin");
                    confidence += 50;
                }
                if ("https://evil-attacker.com".equals(acao)) {
                    issues.add("Origin reflected without validation");
                    confidence += 80;
                }
                if ("true".equalsIgnoreCase(acac) && ("*".equals(acao) || "https://evil-attacker.com".equals(acao))) {
                    issues.add("Credentials allowed with insecure origin");
                    confidence += 95;
                }

                if (!issues.isEmpty()) {
                    boolean isPrivateApi = ctx.headers() != null && 
                                           (ctx.headers().containsKey("Authorization") || 
                                            ctx.headers().keySet().stream().anyMatch(k -> k.toLowerCase().contains("token")));
                    
                    Severity severity = isPrivateApi ? Severity.HIGH : Severity.LOW;
                    
                    if (!isPrivateApi) {
                        issues.add("(Context: Public API. Wildcard CORS is generally safe for public reads)");
                        confidence = Math.min(confidence, 40); // Cap confidence for public APIs
                    }

                    return CheckResult.vulnerable(getCheckName(), severity,
                            "CORS misconfiguration detected",
                            "Issues: " + String.join("; ", issues) + " (Confidence: " + confidence + "%)",
                            "Configure strict CORS: whitelist specific origins, avoid wildcard with credentials.", confidence);
                }
            }
        } catch (Exception e) {
            log.debug("CORS check error: {}", e.getMessage());
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "CORS";
    }
}
