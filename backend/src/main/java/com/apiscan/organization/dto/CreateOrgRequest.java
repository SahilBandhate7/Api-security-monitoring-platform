package com.apiscan.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrgRequest(
        @NotBlank(message = "Organization name is required") @Size(min = 2, max = 255) String name) {
}
