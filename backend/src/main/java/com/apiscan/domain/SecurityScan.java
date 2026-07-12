package com.apiscan.domain;

import com.apiscan.domain.scan.ScanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Security scan entity — represents a single API security scan job.
 */
@Entity
@Table(name = "security_scans", indexes = {
        @Index(name = "idx_scans_org_id", columnList = "org_id"),
        @Index(name = "idx_scans_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class SecurityScan {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "org_id", nullable = false, length = 36)
    private String orgId;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(name = "target_url", nullable = false, columnDefinition = "TEXT")
    private String targetUrl;

    @Column(nullable = false, length = 10)
    private String method;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "jsonb")
    private Map<String, String> requestHeaders;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", columnDefinition = "jsonb")
    private Map<String, Object> requestBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScanStatus status = ScanStatus.PENDING;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "triggered_by_id", nullable = false, length = 36)
    private String triggeredById;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vulnerability> vulnerabilities = new ArrayList<>();
}
