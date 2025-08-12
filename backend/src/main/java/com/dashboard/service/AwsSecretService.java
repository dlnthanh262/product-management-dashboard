package com.dashboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Configuration
public class AwsSecretService {

    private final Environment environment;

    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    @Value("${aws.access.key.id}")
    private String accessKeyId;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.jwt.secret.name}")
    private String jwtSecretName;

    public AwsSecretService(Environment environment) {
        this.environment = environment;
    }

    public String getJwtSecret() {
        String activeProfile = environment.getProperty("spring.profiles.active", "dev");
        if ("dev".equalsIgnoreCase(activeProfile)) {
            String localSecret = System.getenv("JWT_SECRET");
            if (localSecret != null && !localSecret.isBlank()) {
                return localSecret;
            }
            return "N8#uF@3mZ2k%qV!eY6pT*oR1jB7cL$wX9dH&gQ4aS5rM0yC^tP";
        }

        SecretsManagerClient client = SecretsManagerClient.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
            ))
            .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId(jwtSecretName)
            .build();

        return client.getSecretValue(request).secretString();
    }
}
