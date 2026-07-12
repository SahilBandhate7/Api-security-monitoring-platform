package com.apiscan.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

    private String accessSecret;
    private String refreshSecret;
    private int accessExpiryMinutes = 15;
    private int refreshExpiryDays = 7;
}
