package com.apiscan.domain;

import com.apiscan.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Organization member — maps users to orgs with a role.
 */
@Entity
@Table(name = "org_members", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "org_id" }))
@Getter
@Setter
@NoArgsConstructor
public class OrgMember {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.TESTER;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt = LocalDateTime.now();
}
