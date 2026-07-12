package com.apiscan.security;

import com.apiscan.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom UserDetails implementation for Spring Security.
 * Carries user ID, email, and current org context.
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    private final String password;
    private final String orgId;
    private final UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null)
            return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
