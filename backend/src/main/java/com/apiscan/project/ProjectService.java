package com.apiscan.project;

import com.apiscan.domain.Organization;
import com.apiscan.domain.Project;
import com.apiscan.project.dto.CreateProjectRequest;
import com.apiscan.project.dto.ProjectResponse;
import com.apiscan.repository.OrganizationRepository;
import com.apiscan.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public ProjectResponse createProject(String orgId, CreateProjectRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        long count = projectRepository.countByOrganizationId(orgId);
        if (count >= org.getTier().getMaxProjects()) {
            throw new IllegalStateException("Project limit reached for " + org.getTier() + " plan");
        }

        Project project = new Project();
        project.setOrganization(org);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setBaseUrl(request.baseUrl());
        projectRepository.save(project);

        log.info("Project created: {} in org {}", project.getName(), orgId);
        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjects(String orgId, Pageable pageable) {
        return projectRepository.findAllByOrganizationId(orgId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(String projectId, String name, String description, String baseUrl) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        if (name != null)
            project.setName(name);
        if (description != null)
            project.setDescription(description);
        if (baseUrl != null)
            project.setBaseUrl(baseUrl);
        projectRepository.save(project);
        return mapToResponse(project);
    }

    @Transactional
    public void deleteProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        projectRepository.delete(project);
        log.info("Project deleted: {}", projectId);
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getOrganization().getId(),
                project.getName(),
                project.getDescription(),
                project.getBaseUrl(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
