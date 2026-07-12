package com.apiscan.repository;

import com.apiscan.domain.ScanReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScanReportRepository extends JpaRepository<ScanReport, String> {

    Optional<ScanReport> findByScanId(String scanId);
}
