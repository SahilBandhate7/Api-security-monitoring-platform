package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HeaderSecurityCheck implements ScanCheck {

    private record HeaderCheck(String header, String description, String remediation) {
    }

    private static final List<HeaderCheck> REQUIRED_HEADERS = List.of(
            new HeaderCheck("Strict-Transport-Security",
                    "Missing HSTS header", "Add Strict-Transport-Security: max-age=31536000; includeSubDomains"),
            new HeaderCheck("X-Content-Type-Options",
                    "Missing X-Content-Type-Options", "Add X-Content-Type-Options: nosniff"),
            new HeaderCheck("X-Frame-Options",
                    "Missing X-Frame-Options", "Add X-Frame-Options: DENY or SAMEORIGIN"),
            new HeaderCheck("Content-Security-Policy",
                    "Missing Content-Security-Policy", "Define a strict CSP header"),
            new HeaderCheck("X-XSS-Protection",
                    "Missing X-XSS-Protection", "Add X-XSS-Protection: 1; mode=block"));

    @Override
    public CheckResult execute(ScanContext ctx) {
        try {
            ResponseEntity<String> response = ctx.baselineResponse();
            if (response == null) {
                response = ctx.webClient().method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                        .uri(ctx.targetUrl())
                        .headers(h -> {
                            if (ctx.headers() != null)
                                ctx.headers().forEach(h::add);
                        })
                        .retrieve()
                        .toEntity(String.class)
                        .block(Duration.ofSeconds(10));
            }

            HttpHeaders headers = response != null ? response.getHeaders() : new HttpHeaders();
            List<String> missing = new ArrayList<>();

            for (HeaderCheck hc : REQUIRED_HEADERS) {
                if (!headers.containsKey(hc.header())) {
                    missing.add(hc.header());
                }
            }

            if (!missing.isEmpty()) {
                return CheckResult.vulnerable(getCheckName(), Severity.MEDIUM,
                        "Missing security headers: " + String.join(", ", missing),
                        "Headers absent: " + missing,
                        "Add all recommended security headers to responses.");
            }
        } catch (Exception e) {
            log.debug("Header check error: {}", e.getMessage());
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "HEADER_SECURITY";
    }
}
