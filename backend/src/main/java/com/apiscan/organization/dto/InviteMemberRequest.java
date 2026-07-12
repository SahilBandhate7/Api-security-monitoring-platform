package com.apiscan.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteMemberRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        String role // Optional: defaults to TESTER
) {
}
