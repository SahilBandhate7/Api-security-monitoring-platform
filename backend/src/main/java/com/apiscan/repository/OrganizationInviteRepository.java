package com.apiscan.repository;

import com.apiscan.domain.OrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, String> {
    Optional<OrganizationInvite> findByToken(String token);
    List<OrganizationInvite> findByOrganizationIdAndStatus(String organizationId, String status);
}
