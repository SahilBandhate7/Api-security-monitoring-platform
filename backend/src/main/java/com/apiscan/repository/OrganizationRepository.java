package com.apiscan.repository;

import com.apiscan.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
