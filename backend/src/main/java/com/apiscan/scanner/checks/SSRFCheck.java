package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SSRFCheck implements ScanCheck {

    private static final List<String> PAYLOADS = List.of(
            "http://169.254.169.254/latest/meta-data/", // AWS metadata
            "http://127.0.0.1:80",
            "http://localhost:22",
            "http://[::1]:80",
            "http://0x7f000001:80",
            "file:///etc/passwd");

    @Override
    public CheckResult execute(ScanContext ctx) {
        for (String payload : PAYLOADS) {
            try {
                String injectedUrl = ctx.targetUrl() +
                        (ctx.targetUrl().contains("?") ? "&" : "?") +
                        "url=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);

                long startTime = System.currentTimeMillis();
                ResponseEntity<String> response = ctx.webClient().method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                        .uri(injectedUrl)
                        .headers(h -> {
                            if (ctx.headers() != null)
                                ctx.headers().forEach(h::add);
                        })
                        .retrieve()
                        .toEntity(String.class)
                        .block(Duration.ofSeconds(10));
                long duration = System.currentTimeMillis() - startTime;

                if (response == null) continue;
                String body = response.getBody();
                if (body == null) continue;

                // 1. Content-Type Awareness
                List<String> contentTypes = response.getHeaders().get("Content-Type");
                boolean isJson = contentTypes != null && contentTypes.stream().anyMatch(ct -> ct.toLowerCase().contains("application/json"));

                // 2. Reflection Filter
                boolean isReflected = body.contains(payload);
                
                // 3. Multi-Signal Internal Evidence
                boolean hasAwsMetadata = body.contains("\"ami-id\"") || body.contains("\"instance-id\"") || body.contains("latest/meta-data");
                boolean hasDbErrors = body.contains("ECONNREFUSED") || body.contains("Connection refused") || body.contains("root:x:0:0");
                boolean hasNetworkTimeout = duration > 8000; // Suspected blind SSRF timeout

                int confidence = 0;
                
                if (hasAwsMetadata || hasDbErrors) {
                    confidence += 90;
                } else if (body.contains("127.0.0.1") && !isReflected) {
                    confidence += 40;
                }
                
                if (isJson) confidence += 10;
                if (hasNetworkTimeout) confidence += 20;
                
                // If the payload is just reflected back as plain HTML value and no internal signatures exist, it's NOT an SSRF.
                if (isReflected && !hasAwsMetadata && !hasDbErrors && !isJson) {
                    confidence = 0; // Likely just a canonical link or search bar reflection
                }

                if (confidence >= 60) {
                    // Cap at 100
                    int finalConfidence = Math.min(100, confidence);
                    return CheckResult.vulnerable(getCheckName(), Severity.CRITICAL,
                            "Server-Side Request Forgery (SSRF) vulnerability detected",
                            "Payload: " + payload + " triggered internal network signals. (Confidence: " + finalConfidence + "%)",
                            "Validate and sanitize all URLs. Block requests to internal IPs, metadata endpoints, and file:// protocol.",
                            finalConfidence);
                }
            } catch (Exception e) {
                // Timeouts often signify the backend tried to route internally and hit a firewall drop, but without OOB it's low confidence
                log.debug("SSRF check with payload '{}' caused error/timeout: {}", payload, e.getMessage());
            }
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "SSRF";
    }
}
