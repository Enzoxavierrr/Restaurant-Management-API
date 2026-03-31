package com.enzo.restaurant_api.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@ConditionalOnProperty(prefix = "app.firebase", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
        FirebaseOptions.Builder options = FirebaseOptions.builder()
                .setCredentials(loadCredentials(properties));

        if (StringUtils.hasText(properties.getProjectId())) {
            options.setProjectId(properties.getProjectId());
        }

        if (StringUtils.hasText(properties.getDatabaseUrl())) {
            options.setDatabaseUrl(properties.getDatabaseUrl());
        }

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options.build());
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    private GoogleCredentials loadCredentials(FirebaseProperties properties) throws IOException {
        if (StringUtils.hasText(properties.getServiceAccountJson())) {
            try (InputStream inputStream = new ByteArrayInputStream(
                    properties.getServiceAccountJson().getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        if (StringUtils.hasText(properties.getServiceAccountPath())) {
            try (InputStream inputStream = new FileInputStream(properties.getServiceAccountPath())) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException ignored) {
            // Fall through to a clearer startup error for local setup.
        }

        throw new IllegalStateException(
                "Firebase nao configurado. Defina app.firebase.service-account-path, "
                        + "app.firebase.service-account-json ou GOOGLE_APPLICATION_CREDENTIALS.");
    }
}
