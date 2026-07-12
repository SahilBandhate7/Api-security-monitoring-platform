package com.apiscan.security;

import com.apiscan.common.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for method-level RBAC enforcement.
 * Usage: @RequiresRole(UserRole.ADMIN)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    UserRole[] value();
}
