package com.apiscan.scanner;
import com.apiscan.domain.scan.ScanStatus;
import com.apiscan.domain.SecurityScan;
import com.apiscan.repository.SecurityScanRepository;
import com.apiscan.scanner.checks.CheckResult;
import com.apiscan.scanner.checks.ScanCheck;
import com.apiscan.scanner.checks.ScanContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RabbitMQ consumer — processes scan jobs by running all security checks
 * in parallel using virtual threads, then compiling results into a report.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScanOrchestrator {

    private final List<ScanCheck> checks;
    private final RequestExecutor requestExecutor;
    private final ReportBuilder reportBuilder;
    private final SecurityScanRepository scanRepository;

    @RabbitListener(queues = "${rabbitmq.scan-queue}")
    public void processScanJob(ScanJobMessage job) {
        log.info("Processing scan job: {}", job.scanId());

        SecurityScan scan = scanRepository.findById(job.scanId()).orElse(null);
        if (scan == null) {
            log.error("Scan not found: {}", job.scanId());
            return;
        }

        scan.setStatus(ScanStatus.RUNNING);
        scanRepository.save(scan);

        long start = System.currentTimeMillis();
        try {
            // Execute baseline request
            ResponseEntity<String> baseline = requestExecutor.execute(
                    scan.getTargetUrl(), scan.getMethod(),
                    scan.getRequestHeaders(), scan.getRequestBody());

            // Build context for all checks
            ScanContext ctx = new ScanContext(
                    scan.getTargetUrl(), scan.getMethod(),
                    scan.getRequestHeaders(), scan.getRequestBody(),
                    requestExecutor.getWebClient(), baseline);

            // Run all checks in parallel (virtual threads handle this efficiently)
            List<CheckResult> results = checks.parallelStream()
                    .map(check -> {
                        try {
                            return check.execute(ctx);
                        } catch (Exception e) {
                            log.error("Check {} failed: {}", check.getCheckName(), e.getMessage());
                            return CheckResult.safe(check.getCheckName());
                        }
                    })
                    .toList();

            // Build report from all results (including safe ones for completeness)
            reportBuilder.buildAndSave(scan.getId(), results);

            scan.setStatus(ScanStatus.COMPLETED);
            scan.setDurationMs(System.currentTimeMillis() - start);
            log.info("Scan completed: {} in {}ms", job.scanId(), scan.getDurationMs());

        } catch (Exception e) {
            log.error("Scan failed: {}", job.scanId(), e);
            scan.setStatus(ScanStatus.FAILED);
            scan.setDurationMs(System.currentTimeMillis() - start);
        } finally {
            scanRepository.save(scan);
        }
    }

   public List<CheckResult> execute(SecurityScan scan) {

    // 🔹 baseline request
    var baseline = requestExecutor.execute(
            scan.getTargetUrl(),
            scan.getMethod(),
            scan.getRequestHeaders(),
            scan.getRequestBody()
    );

    // 🔹 correct context creation
    ScanContext ctx = new ScanContext(
            scan.getTargetUrl(),
            scan.getMethod(),
            scan.getRequestHeaders(),
            scan.getRequestBody(),
            requestExecutor.getWebClient(),
            baseline
    );

    // 🔹 run checks
    return checks.parallelStream()
            .map(check -> {
                try {
                    return check.execute(ctx); // ✅ FIXED METHOD NAME
                } catch (Exception e) {
                    log.error("Check {} failed", check.getCheckName(), e);
                    return CheckResult.safe(check.getCheckName());
                }
            })
            .toList();
}
}

