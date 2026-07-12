package com.apiscan.scanner.checks;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Context passed to all security checks — contains target info and baseline
 * response.
 */
public record ScanContext(
        String targetUrl,
        String method,
        Map<String, String> headers,
        Map<String, Object> body,
        WebClient webClient,
        ResponseEntity<String> baselineResponse) {
}
