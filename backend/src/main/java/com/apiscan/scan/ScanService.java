package com.apiscan.scan;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.apiscan.domain.scan.ScanStatus;
import com.apiscan.config.RabbitMQConfig;
import com.apiscan.domain.Organization;
import com.apiscan.domain.SecurityScan;
import com.apiscan.domain.Vulnerability;
import com.apiscan.repository.OrganizationRepository;
import com.apiscan.repository.SecurityScanRepository;
import com.apiscan.scan.dto.ScanRequest;
import com.apiscan.scan.dto.ScanResponse;
import com.apiscan.scan.dto.ScanStatusResponse;
import com.apiscan.scanner.ScanJobMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {

    private final SecurityScanRepository scanRepository;
    private final OrganizationRepository organizationRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public ScanResponse triggerScan(String orgId, String userId, ScanRequest request) {
        // Check quota
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        if (org.getUsedQuota() >= org.getMonthlyQuota()) {
            throw new IllegalStateException("Monthly scan quota exceeded. Upgrade your plan.");
        }

        // Create scan record
        SecurityScan scan = new SecurityScan();
        scan.setOrgId(orgId);
        scan.setProjectId(request.projectId());
        scan.setTargetUrl(request.targetUrl());
        scan.setMethod(request.method().toUpperCase());
        scan.setRequestHeaders(request.headers());
        scan.setRequestBody(request.body());
        scan.setTriggeredById(userId);
        scan.setStatus(ScanStatus.PENDING);
        scanRepository.save(scan);

        // Increment quota
        org.setUsedQuota(org.getUsedQuota() + 1);
        organizationRepository.save(org);

        // Dispatch to RabbitMQ
        ScanJobMessage job = new ScanJobMessage(
                scan.getId(), scan.getTargetUrl(), scan.getMethod(),
                scan.getRequestHeaders(), scan.getRequestBody());
        TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.SCAN_EXCHANGE,
                        "scan.new",
                        job
                );
                log.info("Scan job dispatched AFTER COMMIT: {} for {}", scan.getId(), scan.getTargetUrl());
            }
        }
);

        return mapToResponse(scan);
    }

    @Transactional(readOnly = true)
    public Page<ScanResponse> getScans(String orgId, ScanStatus status, Pageable pageable) {
        if (status != null) {
            return scanRepository.findAllByOrgIdAndStatus(orgId, status, pageable)
                    .map(this::mapToResponse);
        }
        return scanRepository.findAllByOrgId(orgId, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ScanResponse getScan(String scanId) {
        SecurityScan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new EntityNotFoundException("Scan not found"));
        return mapToResponse(scan);
    }

    @Transactional(readOnly = true)
    public ScanStatusResponse getScanStatus(String scanId) {
        SecurityScan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new EntityNotFoundException("Scan not found"));
        return new ScanStatusResponse(scan.getId(), scan.getStatus(), scan.getDurationMs());
    }

    private ScanResponse mapToResponse(SecurityScan scan) {
        var vulns = scan.getVulnerabilities().stream()
                .map(v -> new ScanResponse.VulnInfo(
                        v.getId(), v.getType(), v.getSeverity().name(),
                        v.getDescription(), v.getEvidence(), v.getRemediation()))
                .toList();

        return new ScanResponse(
                scan.getId(), scan.getOrgId(), scan.getProjectId(),
                scan.getTargetUrl(), scan.getMethod(), scan.getStatus(),
                scan.getDurationMs(), scan.getCreatedAt(), vulns);
    }
}
