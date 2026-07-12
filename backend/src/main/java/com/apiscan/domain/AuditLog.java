package com.apiscan.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Audit log entity — tracks sensitive actions within an organization.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_org_id", columnList = "org_id"),
        @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "org_id", nullable = false, length = 36)
    private String orgId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 100)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
