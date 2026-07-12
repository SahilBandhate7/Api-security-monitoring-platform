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
public class SQLInjectionCheck implements ScanCheck {

    private static final List<String> PAYLOADS = List.of(
            "' OR '1'='1",
            "1; DROP TABLE users--",
            "' UNION SELECT NULL,NULL--",
            "1' AND SLEEP(5)--",
            "' OR 1=1--",
            "1' ORDER BY 1--");

    private static final List<String> ERROR_PATTERNS = List.of(
            "sql syntax", "mysql_fetch", "ora-", "pg_query",
            "unclosed quotation", "sqlite3::", "syntax error",
            "microsoft sql", "odbc driver", "jdbc");

    @Override
    public CheckResult execute(ScanContext ctx) {
        for (String payload : PAYLOADS) {
            try {
                String injectedUrl = ctx.targetUrl() +
                        (ctx.targetUrl().contains("?") ? "&" : "?") +
                        "id=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);

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
                boolean isVulnerable = ERROR_PATTERNS.stream()
                        .anyMatch(p -> body != null && body.toLowerCase().contains(p));

                if (isVulnerable) {
                    return CheckResult.vulnerable(getCheckName(), Severity.CRITICAL,
                            "SQL Injection vulnerability detected",
                            "Payload: " + payload + " triggered a database error in the response",
                            "Use parameterized queries / prepared statements. Never concatenate user input into SQL.");
                }
            } catch (Exception e) {
                log.debug("SQLi check error with payload '{}': {}", payload, e.getMessage());
            }
        }
        return CheckResult.safe(getCheckName());
    }

    @Override
    public String getCheckName() {
        return "SQL_INJECTION";
    }
}
