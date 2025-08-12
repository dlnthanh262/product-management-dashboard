package com.dashboard.config;

import com.dashboard.service.AwsSecretService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    private final AwsSecretService awsSecretService;

    public JwtConfig(AwsSecretService awsSecretService) {
        this.awsSecretService = awsSecretService;
    }

    @Bean
    public String jwtSecret() {
        return awsSecretService.getJwtSecret();
    }
}