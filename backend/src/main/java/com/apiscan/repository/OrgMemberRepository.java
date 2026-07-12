package com.apiscan.repository;

import com.apiscan.domain.OrgMember;
import com.apiscan.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgMemberRepository extends JpaRepository<OrgMember, String> {

    List<OrgMember> findAllByOrganizationId(String orgId);

    List<OrgMember> findAllByUserId(String userId);

    Optional<OrgMember> findByUserIdAndOrganizationId(String userId, String orgId);

    boolean existsByUserIdAndOrganizationId(String userId, String orgId);

    long countByOrganizationId(String orgId);

    long countByOrganizationIdAndRole(String orgId, UserRole role);
}
