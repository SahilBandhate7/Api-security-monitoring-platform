-- ================================================
-- V2: Organizations & Members
-- ================================================

CREATE TABLE organizations (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name                VARCHAR(255) NOT NULL,
    slug                VARCHAR(100) NOT NULL UNIQUE,
    tier                VARCHAR(20)  NOT NULL DEFAULT 'FREE',
    stripe_customer_id  VARCHAR(255) UNIQUE,
    monthly_quota       INT          NOT NULL DEFAULT 50,
    used_quota          INT          NOT NULL DEFAULT 0,
    quota_reset_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_organizations_slug ON organizations(slug);

CREATE TABLE org_members (
    id         VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id    VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    org_id     VARCHAR(36)  NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    role       VARCHAR(20)  NOT NULL DEFAULT 'TESTER',
    invited_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, org_id)
);

CREATE INDEX idx_org_members_user_id ON org_members(user_id);
CREATE INDEX idx_org_members_org_id ON org_members(org_id);
