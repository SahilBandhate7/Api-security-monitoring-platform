package com.apiscan.common.enums;

/**
 * User roles within an organization.
 * Defines the RBAC hierarchy: OWNER > ADMIN > TESTER.
 */
public enum UserRole {
    OWNER,
    ADMIN,
    TESTER
}
