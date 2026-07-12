package com.apiscan.scan;

import com.apiscan.common.ApiResponse;
import com.apiscan.domain.scan.ScanStatus;
import com.apiscan.scan.dto.ScanRequest;
import com.apiscan.scan.dto.ScanResponse;
import com.apiscan.scan.dto.ScanStatusResponse;
import com.apiscan.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs/{orgId}/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScanResponse>> triggerScan(
            @PathVariable String orgId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ScanRequest request) {
        ScanResponse response = scanService.triggerScan(orgId, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Scan triggered", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScanResponse>>> getScans(
            @PathVariable String orgId,
            @RequestParam(required = false) ScanStatus status,
            Pageable pageable) {
        Page<ScanResponse> page = scanService.getScans(orgId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<ApiResponse<ScanResponse>> getScan(@PathVariable String scanId) {
        ScanResponse response = scanService.getScan(scanId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{scanId}/status")
    public ResponseEntity<ApiResponse<ScanStatusResponse>> getScanStatus(@PathVariable String scanId) {
        ScanStatusResponse response = scanService.getScanStatus(scanId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
