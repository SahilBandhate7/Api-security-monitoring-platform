package com.apiscan.scan.dto;

import com.apiscan.domain.scan.ScanStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ScanResponse(
        String id,
        String orgId,
        String projectId,
        String targetUrl,
        String method,
        ScanStatus status,
        Long durationMs,
        LocalDateTime createdAt,
        List<VulnInfo> vulnerabilities) {
    public record VulnInfo(
            String id,
            String type,
            String severity,
            String description,
            String evidence,
            String remediation) {
    }
}
