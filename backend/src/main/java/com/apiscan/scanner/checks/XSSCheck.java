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
public class XSSCheck implements ScanCheck {

    private static final List<String> PAYLOADS = List.of(
            "<script>alert('xss')</script>",
            "<img src=x onerror=alert(1)>",
            "'\"><script>alert(1)</script>",
            "<svg/onload=alert(1)>",
            "javascript:alert(1)");

    @Override
    public CheckResult execute(ScanContext ctx) {
        for (String payload : PAYLOADS) {
            try {
                String injectedUrl = ctx.targetUrl() +
                        (ctx.targetUrl().contains("?") ? "&" : "?") +
                        "q=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);

                ResponseEntity<String> response = ctx.webClient().method(org.springframework.http.HttpMethod.valueOf(ctx.method().toUpperCase()))
                        .uri(injectedUrl)
                        .headers(h -> {
                            if (ctx.headers() != null)
                                ctx.headers().forEach(h::add);
                        })
                        .retrieve()
                        .toEntity(String.class)
                        .block(Duration.ofSeconds(10));

                String body = response != null ? response.getBody() : "";
                if (body != null && body.contains(payload)) {
                    return CheckResult.vulnerable(getCheckName(), Severity.HIGH,
                            "Cross-Site Scripting (XSS) vulnerability detected - payload reflected in response",
                            "Payload: " + payload + " was reflected without sanitization",
                            "Sanitize and encode all user input before rendering. Use Content-Security-Policy headers.");
                }
            } catch (Exception e) {
                log.debug("XSS check error: {}", e.getMessage());
            }
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "XSS";
    }
}
