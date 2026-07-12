package com.apiscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * APIScan — API Security Testing & Monitoring Platform
 *
 * Main entry point for the Spring Boot application.
 */
@SpringBootApplication
@EnableAsync
public class ApiScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiScanApplication.class, args);
    }
}
