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
        // Opção 1: JSON completo como string
        if (StringUtils.hasText(properties.getServiceAccountJson())) {
            try (InputStream inputStream = new ByteArrayInputStream(
                    properties.getServiceAccountJson().getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        // Opção 2: Caminho para o arquivo JSON
        if (StringUtils.hasText(properties.getServiceAccountPath())) {
            try (InputStream inputStream = new FileInputStream(properties.getServiceAccountPath())) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }

        // Opção 3: Credenciais individuais via variáveis de ambiente
        if (StringUtils.hasText(properties.getClientEmail()) && StringUtils.hasText(properties.getPrivateKey())) {
            return credentialsFromFields(properties);
        }

        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException ignored) {
            // Fall through to a clearer startup error for local setup.
        }

        throw new IllegalStateException(
                "Firebase nao configurado. Defina FIREBASE_CLIENT_EMAIL + FIREBASE_PRIVATE_KEY, "
                        + "FIREBASE_SERVICE_ACCOUNT_PATH, FIREBASE_SERVICE_ACCOUNT_JSON "
                        + "ou GOOGLE_APPLICATION_CREDENTIALS.");
    }

    /**
     * Monta um JSON de service account a partir dos campos individuais do .env
     * e usa GoogleCredentials.fromStream() para autenticar.
     */
    private GoogleCredentials credentialsFromFields(FirebaseProperties p) throws IOException {
        // Normaliza a private key: converte \n literais em quebras de linha reais,
        // depois re-escapa para JSON (\n → \\n dentro da string JSON)
        String normalizedKey = p.getPrivateKey()
                .replace("\\n", "\n")   // converte escape literal em newline real
                .replace("\n", "\\n");  // re-escapa para uso dentro de JSON string

        String tokenUri = StringUtils.hasText(p.getTokenUri())
                ? p.getTokenUri()
                : "https://oauth2.googleapis.com/token";

        String json = String.format("""
                {
                  "type": "service_account",
                  "project_id": "%s",
                  "private_key_id": "%s",
                  "private_key": "%s",
                  "client_email": "%s",
                  "client_id": "%s",
                  "token_uri": "%s"
                }
                """,
                p.getProjectId(),
                p.getPrivateKeyId(),
                normalizedKey,
                p.getClientEmail(),
                p.getClientId(),
                tokenUri
        );

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            return GoogleCredentials.fromStream(is);
        }
    }
}
