package com.apiscan.scanner;

import com.apiscan.common.enums.Severity;
import com.apiscan.domain.ScanReport;
import com.apiscan.domain.SecurityScan;
import com.apiscan.domain.Vulnerability;
import com.apiscan.repository.ScanReportRepository;
import com.apiscan.repository.SecurityScanRepository;
import com.apiscan.repository.VulnerabilityRepository;
import com.apiscan.scanner.checks.CheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compiles scan check results into Vulnerability entities and a ScanReport.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportBuilder {

    private final VulnerabilityRepository vulnerabilityRepository;
    private final ScanReportRepository scanReportRepository;
    private final SecurityScanRepository scanRepository;

    @Transactional
    public void buildAndSave(String scanId, List<CheckResult> results) {
        
        SecurityScan scan = scanRepository.findById(scanId).orElseThrow();
    

        // Save vulnerabilities
         List<Vulnerability> vulnerabilities = results.stream()
            .filter(CheckResult::vulnerable)
            .map(result -> {
                Vulnerability vuln = new Vulnerability();
                vuln.setId(java.util.UUID.randomUUID().toString()); // if needed
                vuln.setScan(scan);
                vuln.setType(result.checkName());
                vuln.setSeverity(result.severity());
                vuln.setDescription(result.description());
                vuln.setEvidence(result.evidence());
                vuln.setRemediation(result.remediation());
                vuln.setConfidence(result.confidence());
                return vuln;
            })
            .toList();

        vulnerabilityRepository.saveAll(vulnerabilities);


        // Build summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalChecks", results.size());
        summary.put("vulnerabilitiesFound", results.stream().filter(CheckResult::vulnerable).count());

        Map<String, Long> bySeverity = new HashMap<>();
        for (Severity s : Severity.values()) {
            long count = results.stream()
                    .filter(r -> r.vulnerable() && r.severity() == s)
                    .count();
            if (count > 0)
                bySeverity.put(s.name(), count);
        }
        summary.put("bySeverity", bySeverity);

        Map<String, Object> checkSummaries = new HashMap<>();
        for (CheckResult r : results) {
            Map<String, Object> cs = new HashMap<>();
            cs.put("vulnerable", r.vulnerable());
            cs.put("severity", r.severity().name());
            cs.put("description", r.description());
            checkSummaries.put(r.checkName(), cs);
        }
        summary.put("checks", checkSummaries);

        // Save report
        ScanReport report = new ScanReport();
        report.setScanId(scanId);
        report.setSummary(summary);
        scanReportRepository.save(report);

        log.info("Report built for scan {}: {} vulnerabilities found", scanId,
                results.stream().filter(CheckResult::vulnerable).count());
    }
}
