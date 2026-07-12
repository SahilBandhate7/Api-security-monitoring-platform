package com.apiscan.scanner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

/**
 * Executes HTTP requests against target URLs for the scanner engine.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestExecutor {

    private final WebClient webClient;

    /**
     * Execute a baseline request to the target URL.
     */
    public ResponseEntity<String> execute(String url, String method,
            Map<String, String> headers,
            Map<String, Object> body) {
        try {
            WebClient.RequestHeadersSpec<?> spec;

            switch (method.toUpperCase()) {
                case "POST" -> spec = webClient.post()
                        .uri(url)
                        .headers(h -> {
                            if (headers != null)
                                headers.forEach(h::add);
                        })
                        .bodyValue(body != null ? body : Map.of());
                case "PUT" -> spec = webClient.put()
                        .uri(url)
                        .headers(h -> {
                            if (headers != null)
                                headers.forEach(h::add);
                        })
                        .bodyValue(body != null ? body : Map.of());
                case "DELETE" -> spec = webClient.delete()
                        .uri(url)
                        .headers(h -> {
                            if (headers != null)
                                headers.forEach(h::add);
                        });
                case "PATCH" -> spec = webClient.patch()
                        .uri(url)
                        .headers(h -> {
                            if (headers != null)
                                headers.forEach(h::add);
                        })
                        .bodyValue(body != null ? body : Map.of());
                default -> spec = webClient.get()
                        .uri(url)
                        .headers(h -> {
                            if (headers != null)
                                headers.forEach(h::add);
                        });
            }

            return spec.retrieve()
                    .toEntity(String.class)
                    .block(Duration.ofSeconds(30));
        } catch (Exception e) {
            log.error("Request execution failed for {}: {}", url, e.getMessage());
            return null;
        }
    }

    public WebClient getWebClient() {
        return webClient;
    }
}
