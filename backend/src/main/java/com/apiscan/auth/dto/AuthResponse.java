package com.apiscan.auth.dto;

/**
 * Authentication response — returns tokens after login/signup.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String userId,
        String email,
        String name) {
}
