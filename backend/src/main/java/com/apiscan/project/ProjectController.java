package com.apiscan.project;

import com.apiscan.common.ApiResponse;
import com.apiscan.common.enums.UserRole;
import com.apiscan.project.dto.CreateProjectRequest;
import com.apiscan.project.dto.ProjectResponse;
import com.apiscan.security.RequiresRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orgs/{orgId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @PathVariable String orgId, @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjects(
            @PathVariable String orgId, Pageable pageable) {
        Page<ProjectResponse> page = projectService.getProjects(orgId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable String projectId) {
        ProjectResponse response = projectService.getProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{projectId}")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable String projectId, @RequestBody Map<String, String> request) {
        ProjectResponse response = projectService.updateProject(
                projectId, request.get("name"), request.get("description"), request.get("baseUrl"));
        return ResponseEntity.ok(ApiResponse.success("Project updated", response));
    }

    @DeleteMapping("/{projectId}")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted"));
    }
}
