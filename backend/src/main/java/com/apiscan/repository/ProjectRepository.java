package com.apiscan.repository;

import com.apiscan.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Page<Project> findAllByOrganizationId(String orgId, Pageable pageable);

    long countByOrganizationId(String orgId);
}
