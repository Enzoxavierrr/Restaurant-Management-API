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

    // Credenciais individuais (alternativa ao arquivo JSON)
    private String privateKeyId;
    private String privateKey;
    private String clientEmail;
    private String clientId;
    private String tokenUri = "https://oauth2.googleapis.com/token";
}
