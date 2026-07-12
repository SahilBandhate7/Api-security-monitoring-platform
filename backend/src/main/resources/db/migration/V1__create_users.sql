-- ================================================
-- V1: Users table
-- ================================================

CREATE TABLE users (
    id            VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    avatar_url    VARCHAR(500),
    is_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
