package com.enzo.restaurant_api.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {

    private static final String DEFAULT_SECRET = "default-secret-key-123456789012345678901234567890";
    private static final long DEFAULT_EXPIRATION = 3_600_000L;

    private String secret = DEFAULT_SECRET;
    private Long expiration = DEFAULT_EXPIRATION;

    @PostConstruct
    public void ensureDefaults() {
        if (!StringUtils.hasText(secret)) {
            secret = DEFAULT_SECRET;
        }

        if (expiration == null || expiration <= 0) {
            expiration = DEFAULT_EXPIRATION;
        }
    }
}
