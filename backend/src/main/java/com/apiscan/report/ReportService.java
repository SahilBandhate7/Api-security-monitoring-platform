package com.apiscan.report;

import com.apiscan.domain.ScanReport;
import com.apiscan.domain.SecurityScan;
import com.apiscan.domain.Vulnerability;
import com.apiscan.repository.ScanReportRepository;
import com.apiscan.repository.SecurityScanRepository;
import com.apiscan.repository.VulnerabilityRepository;
import com.apiscan.scan.dto.VulnerabilityResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ScanReportRepository scanReportRepository;
    private final SecurityScanRepository scanRepository;
    private final VulnerabilityRepository vulnerabilityRepository;

    public Map<String, Object> getReport(String scanId) {

        // 🔹 Fetch scan
        SecurityScan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new EntityNotFoundException("Scan not found"));

        // 🔹 Fetch summary report
        ScanReport report = scanReportRepository.findByScanId(scanId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // 🔹 Fetch vulnerabilities
        List<VulnerabilityResponse> vulnerabilities =  vulnerabilityRepository.findByScanId(scanId)
            .stream()
            .map(v -> new VulnerabilityResponse(
                v.getId(),
                v.getType(),
                v.getSeverity().name(),
                v.getDescription(),
                v.getEvidence(),
                v.getRemediation(),
                v.getConfidence()
            ))
            .toList();

        // 🔹 Build response
        Map<String, Object> response = new HashMap<>();
        response.put("scanId", scanId);
        response.put("status", scan.getStatus());
        response.put("duration", scan.getDurationMs());
        response.put("summary",
        report.getSummary() != null ? report.getSummary() : new HashMap<>());
        response.put("duration",
        scan.getDurationMs() != null ? scan.getDurationMs() : 0);
        response.put("vulnerabilities", vulnerabilities);

        return response;
    }
}