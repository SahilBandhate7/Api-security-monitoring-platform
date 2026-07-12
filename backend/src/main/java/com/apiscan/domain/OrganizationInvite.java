package com.apiscan.domain;

import com.apiscan.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organization_invites", indexes = {
        @Index(name = "idx_org_invites_token", columnList = "token", unique = true),
        @Index(name = "idx_org_invites_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
public class OrganizationInvite {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED, EXPIRED

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
