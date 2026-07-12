package com.apiscan.scanner.checks;

import com.apiscan.common.enums.Severity;

/**
 * Result of a single security check.
 */
public record CheckResult(
        String checkName,
        boolean vulnerable,
        Severity severity,
        String description,
        String evidence,
        String remediation,
        Integer confidence) {
    public static CheckResult safe(String checkName) {
        return new CheckResult(checkName, false, Severity.INFO,
                "No vulnerability detected", null, null, 100);
    }

    public static CheckResult vulnerable(String checkName, Severity severity,
            String description, String evidence, String remediation) {
        return new CheckResult(checkName, true, severity, description, evidence, remediation, 100);
    }
    
    public static CheckResult vulnerable(String checkName, Severity severity,
            String description, String evidence, String remediation, Integer confidence) {
        return new CheckResult(checkName, true, severity, description, evidence, remediation, confidence);
    }
}
