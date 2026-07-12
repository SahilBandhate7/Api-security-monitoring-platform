package com.apiscan.project.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
        String id,
        String orgId,
        String name,
        String description,
        String baseUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
