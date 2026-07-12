package com.apiscan.scan.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record ScanRequest(
        @NotBlank(message = "Target URL is required") String targetUrl,

        @NotBlank(message = "HTTP method is required") String method,

        Map<String, String> headers,

        Map<String, Object> body,

        String projectId) {
}
