package com.apiscan.auth;

import com.apiscan.auth.dto.AuthResponse;
import com.apiscan.auth.dto.LoginRequest;
import com.apiscan.auth.dto.SignupRequest;
import com.apiscan.domain.RefreshToken;
import com.apiscan.domain.User;
import com.apiscan.repository.RefreshTokenRepository;
import com.apiscan.repository.UserRepository;
import com.apiscan.organization.OrganizationService;
import com.apiscan.organization.dto.CreateOrgRequest;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Authentication service — handles signup, login, token refresh, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationService organizationService;

    /**
     * Register a new user account and create their base organization.
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        // Create organization for the user
        organizationService.createOrganization(user.getId(), new CreateOrgRequest(request.orgName()));

        log.info("New user registered: {}", user.getEmail());
        return generateTokens(user);
    }

    /**
     * Authenticate user with email and password.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in: {}", user.getEmail());
        return generateTokens(user);
    }

    /**
     * Issue new access token from a valid refresh token.
     */
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        Claims claims = jwtProvider.parseRefreshToken(refreshToken);
        String userId = claims.getSubject();

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new BadCredentialsException("Refresh token expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Rotate refresh token
        refreshTokenRepository.delete(storedToken);

        log.info("Token refreshed for user: {}", user.getEmail());
        return generateTokens(user);
    }

    /**
     * Revoke all refresh tokens for a user (logout).
     */
    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User logged out: {}", userId);
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        // Persist refresh token
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUserId(user.getId());
        tokenEntity.setToken(refreshToken);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(tokenEntity);

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName());
    }
}
