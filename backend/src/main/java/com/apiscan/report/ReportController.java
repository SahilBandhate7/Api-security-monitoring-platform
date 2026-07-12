package com.apiscan.report;

import com.apiscan.common.ApiResponse;
import com.apiscan.common.enums.UserRole;
import com.apiscan.security.RequiresRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orgs/{orgId}/scans/{scanId}")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final PdfExportService pdfExportService;

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReport(
            @PathVariable String orgId,
            @PathVariable String scanId) {
        Map<String, Object> report = reportService.getReport(scanId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/export")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<byte[]> exportPdf(@PathVariable String scanId) {
        byte[] pdf = pdfExportService.generatePdf(scanId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scan-report-" + scanId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
