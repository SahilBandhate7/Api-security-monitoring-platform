package com.apiscan.auth;

import com.apiscan.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT token provider — sign, verify, and parse access/refresh tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtConfig jwtConfig;

    /**
     * Generate an access token for the given user.
     */
    public String generateAccessToken(String userId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtConfig.getAccessExpiryMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getAccessKey())
                .compact();
    }

    /**
     * Generate a refresh token for the given user.
     */
    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtConfig.getRefreshExpiryDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getRefreshKey())
                .compact();
    }

    /**
     * Validate and parse an access token.
     */
    public Claims parseAccessToken(String token) {
        return parseToken(token, getAccessKey());
    }

    /**
     * Validate and parse a refresh token.
     */
    public Claims parseRefreshToken(String token) {
        return parseToken(token, getRefreshKey());
    }

    /**
     * Extract user ID from a valid access token.
     */
    public String getUserIdFromAccessToken(String token) {
        return parseAccessToken(token).getSubject();
    }

    /**
     * Check if a token is valid (not expired, well-formed).
     */
    public boolean isTokenValid(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getAccessKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getRefreshKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }
}
