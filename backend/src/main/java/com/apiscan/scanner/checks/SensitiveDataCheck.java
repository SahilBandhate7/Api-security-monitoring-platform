package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SensitiveDataCheck implements ScanCheck {

    private record DataPattern(String name, Pattern pattern) {
    }

    private static final List<DataPattern> PATTERNS = List.of(
            new DataPattern("API Key (Stripe / Generic)",
                    Pattern.compile("(?i)(sk_live_[a-zA-Z0-9]{20,}|api[_-]?key[\"':\\s]*[\"']?([a-zA-Z0-9]{20,})[\"']?)")),
            new DataPattern("Authorization Bearer",
                    Pattern.compile("(?i)(Authorization:\\s*Bearer\\s+[a-zA-Z0-9_\\-\\.]+|ey[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]+)")),
            new DataPattern("Password field",
                    Pattern.compile("(?i)(password|passwd|pwd)[\"':\\s]*[\"']?[^\"'\\s]{4,}[\"']?")),
            new DataPattern("AWS Access Key", Pattern.compile("AKIA[0-9A-Z]{16}")),
            new DataPattern("Private Key", Pattern.compile("-----BEGIN (RSA |EC )?PRIVATE KEY-----")),
            // Requires strict key context for JSON matching to prevent false positives
            new DataPattern("Email Address", Pattern.compile("(?i)(\"userEmail\"|\"customerEmail\"|\"email\")[\\s:]*\"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\"")),
            new DataPattern("Credit Card", Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b")));

    public boolean isValidCard(String number) {
        String cleanNumber = number.replaceAll("[\\s-]", "");
        if (!cleanNumber.matches("\\d{13,16}")) return false;
        
        int sum = 0;
        boolean alternate = false;
        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cleanNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

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

            if (response == null) return CheckResult.safe(getCheckName());

            String body = response.getBody();
            if (body == null || body.isEmpty())
                return CheckResult.safe(getCheckName());

            // 1. Content-Type Filtering
            List<String> contentTypes = response.getHeaders().get("Content-Type");
            boolean isJson = contentTypes != null && contentTypes.stream().anyMatch(ct -> ct.toLowerCase().contains("application/json"));
            
            // If it's a raw HTML webpage, scanning for emails and basic text patterns will trigger extreme false positives.
            // Downgrade confidence heavily or skip generic checks.
            int baseConfidence = isJson ? 90 : 30;

            List<String> found = new ArrayList<>();
            for (DataPattern dp : PATTERNS) {
                java.util.regex.Matcher matcher = dp.pattern().matcher(body);
                while (matcher.find()) {
                    String match = matcher.group();
                    if (dp.name().equals("Credit Card")) {
                        if (isValidCard(match)) {
                            found.add(dp.name());
                            baseConfidence += 10; // Valid Luhn increases confidence
                            break; 
                        }
                    } else if (dp.name().equals("Email Address")) {
                        if (isJson) {
                            found.add(dp.name());
                        }
                    } else {
                        found.add(dp.name());
                        break;
                    }
                }
            }

            if (!found.isEmpty()) {
                // Remove duplicates
                List<String> uniqueFound = found.stream().distinct().toList();
                // Confidence capped at 100
                int finalConfidence = Math.min(100, baseConfidence);
                
                // If it's HTML and we only found emails (which didn't match JSON keys anyway), we probably shouldn't flag it as HIGH.
                Severity sev = finalConfidence > 60 ? Severity.HIGH : Severity.LOW;
                
                if (finalConfidence >= 60) {
                    return CheckResult.vulnerable(getCheckName(), sev,
                            "Sensitive data exposure detected in response",
                            "Found: " + String.join(", ", uniqueFound) + " (Confidence: " + finalConfidence + "%)",
                            "Remove sensitive data from API responses. Use field filtering, redaction, and proper access controls.", 
                            finalConfidence);
                }
            }
        } catch (Exception e) {
            log.debug("Sensitive data check error: {}", e.getMessage());
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "SENSITIVE_DATA";
    }
}
