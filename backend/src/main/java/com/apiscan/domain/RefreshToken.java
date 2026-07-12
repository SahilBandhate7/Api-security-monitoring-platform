package com.apiscan.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Refresh token entity — persisted JWT refresh tokens for session management.
 */
@Entity
@Table(name = "refresh_tokens", indexes = @Index(name = "idx_refresh_tokens_token", columnList = "token"))
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
