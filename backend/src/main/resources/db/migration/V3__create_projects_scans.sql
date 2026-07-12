-- ================================================
-- V3: Projects & Security Scans
-- ================================================

CREATE TABLE projects (
    id          VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    org_id      VARCHAR(36)  NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    base_url    VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_projects_org_id ON projects(org_id);

CREATE TABLE security_scans (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    org_id          VARCHAR(36)  NOT NULL REFERENCES organizations(id),
    project_id      VARCHAR(36)  REFERENCES projects(id) ON DELETE SET NULL,
    target_url      TEXT         NOT NULL,
    method          VARCHAR(10)  NOT NULL,
    request_headers JSONB,
    request_body    JSONB,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    duration_ms     BIGINT,
    triggered_by_id VARCHAR(36)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scans_org_id ON security_scans(org_id);
CREATE INDEX idx_scans_project_id ON security_scans(project_id);
CREATE INDEX idx_scans_status ON security_scans(status);
CREATE INDEX idx_scans_created_at ON security_scans(created_at DESC);
