package com.apiscan.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "Project name is required") @Size(min = 2, max = 255) String name,

        String description,

        String baseUrl) {
}
