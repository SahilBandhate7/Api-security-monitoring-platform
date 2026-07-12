package com.apiscan.scanner.checks;

/**
 * Interface for all security scan checks.
 * Implement this to add a new check to the scanning engine.
 */
public interface ScanCheck {

    /**
     * Execute the security check against the target.
     */
    CheckResult execute(ScanContext context);

    /**
     * Return the unique name identifying this check.
     */
    String getCheckName();
}
