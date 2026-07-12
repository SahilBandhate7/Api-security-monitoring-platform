package com.apiscan.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Type-safe binding for custom application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private String frontendUrl = "http://localhost:3000";
    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins = "http://localhost:3000";
    }
}
