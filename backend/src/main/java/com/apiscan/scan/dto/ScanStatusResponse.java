package com.apiscan.scan.dto;

import com.apiscan.domain.scan.ScanStatus; 

public record ScanStatusResponse(
        String scanId,
        ScanStatus status,
        Long durationMs) {
}
