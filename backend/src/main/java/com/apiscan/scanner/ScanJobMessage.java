package com.apiscan.scanner;

import java.io.Serializable;
import java.util.Map;

/**
 * RabbitMQ message payload for scan jobs.
 */
public record ScanJobMessage(
        String scanId,
        String targetUrl,
        String method,
        Map<String, String> headers,
        Map<String, Object> body) implements Serializable {
}
