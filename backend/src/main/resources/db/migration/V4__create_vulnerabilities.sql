-- ================================================
-- V4: Vulnerabilities & Scan Reports
-- ================================================

CREATE TABLE vulnerabilities (
    id          VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    scan_id     VARCHAR(36)  NOT NULL REFERENCES security_scans(id) ON DELETE CASCADE,
    type        VARCHAR(100) NOT NULL,
    severity    VARCHAR(20)  NOT NULL,
    description TEXT         NOT NULL,
    evidence    TEXT,
    remediation TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vulnerabilities_scan_id ON vulnerabilities(scan_id);
CREATE INDEX idx_vulnerabilities_severity ON vulnerabilities(severity);

CREATE TABLE scan_reports (
    id         VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    scan_id    VARCHAR(36) NOT NULL UNIQUE REFERENCES security_scans(id) ON DELETE CASCADE,
    summary    JSONB       NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scan_reports_scan_id ON scan_reports(scan_id);
