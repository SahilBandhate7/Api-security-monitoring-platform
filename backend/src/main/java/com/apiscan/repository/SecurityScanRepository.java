package com.apiscan.repository;

import com.apiscan.domain.scan.ScanStatus;
import com.apiscan.domain.SecurityScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityScanRepository extends JpaRepository<SecurityScan, String> {

    Page<SecurityScan> findAllByOrgId(String orgId, Pageable pageable);

    Page<SecurityScan> findAllByOrgIdAndStatus(String orgId, ScanStatus status, Pageable pageable);

    Page<SecurityScan> findAllByOrgIdAndProjectId(String orgId, String projectId, Pageable pageable);

    List<SecurityScan> findTop10ByOrgIdOrderByCreatedAtDesc(String orgId);

    long countByOrgId(String orgId);

    long countByOrgIdAndStatus(String orgId, ScanStatus status);

    @Query("SELECT s.status, COUNT(s) FROM SecurityScan s WHERE s.orgId = :orgId GROUP BY s.status")
    List<Object[]> countByOrgIdGroupByStatus(@Param("orgId") String orgId);
}
