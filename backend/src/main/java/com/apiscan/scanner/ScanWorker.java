package com.apiscan.scanner;

import com.apiscan.domain.SecurityScan;
import com.apiscan.domain.scan.ScanStatus;
import com.apiscan.repository.SecurityScanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanWorker {

    private final SecurityScanRepository scanRepository;
    private final ScanOrchestrator scanOrchestrator; // 🔥 inject this

    @RabbitListener(queues = "scan.queue")
    public void processScan(ScanJobMessage job) {

        log.info("Received scan job: {}", job.scanId());

        SecurityScan scan = scanRepository.findById(job.scanId())
                .orElseThrow(() -> new RuntimeException("Scan not found"));

        long start = System.currentTimeMillis();

        try {
            // 🔄 STEP 1: RUNNING
            scan.setStatus(ScanStatus.RUNNING);
            scanRepository.save(scan);

            // 🚀 STEP 2: REAL SCAN EXECUTION
            var results = scanOrchestrator.execute(scan);

            // 💾 STEP 3: SAVE RESULTS
            scan.setStatus(ScanStatus.COMPLETED);
            scan.setDurationMs(System.currentTimeMillis() - start);

            scanRepository.save(scan);

            log.info("Scan completed: {}", scan.getId());

        } catch (Exception e) {

            scan.setStatus(ScanStatus.FAILED);
            scanRepository.save(scan);

            log.error("Scan failed: {}", scan.getId(), e);
        }
    }
}