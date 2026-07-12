-- ================================================
-- V5: Subscriptions, Refresh Tokens & Audit Logs
-- ================================================

CREATE TABLE subscriptions (
    id                      VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    org_id                  VARCHAR(36)  NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    stripe_subscription_id  VARCHAR(255) NOT NULL UNIQUE,
    tier                    VARCHAR(20)  NOT NULL,
    status                  VARCHAR(50)  NOT NULL,
    current_period_end      TIMESTAMP    NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_org_id ON subscriptions(org_id);

CREATE TABLE refresh_tokens (
    id         VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id    VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token      TEXT         NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE audit_logs (
    id         VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    org_id     VARCHAR(36)  NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id    VARCHAR(36)  NOT NULL,
    action     VARCHAR(100) NOT NULL,
    metadata   JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_org_id ON audit_logs(org_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
