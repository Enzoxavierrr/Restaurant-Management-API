package com.enzo.restaurant_api.config.firebase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.firebase")
public class FirebaseProperties {

    private boolean enabled = true;
    private String projectId;
    private String databaseUrl;
    private String serviceAccountPath;
    private String serviceAccountJson;
}
