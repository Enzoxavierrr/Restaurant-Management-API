package com.enzo.restaurant_api;

import com.enzo.restaurant_api.config.JwtProperties;
import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    private MockMvc mockMvc;
    private SecretKey secretKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldReturnJwtWhenCredentialsValid() throws Exception {
        userRepository.save(User.builder()
                .name("Auth Test")
                .email("auth@example.com")
                .password(passwordEncoder.encode("strongPass1"))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build());

        String payload = """
                {
                  "email": "auth@example.com",
                  "password": "strongPass1"
                }
                """;

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        Map<String, String> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        String token = body.get("token");

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        assertThat(claims.getBody().getSubject()).isEqualTo("auth@example.com");
    }

    @Test
    void shouldReturn401ForInvalidCredentials() throws Exception {
        userRepository.save(User.builder()
                .name("Auth Test")
                .email("auth@example.com")
                .password(passwordEncoder.encode("strongPass1"))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build());

        String payload = """
                {
                  "email": "auth@example.com",
                  "password": "wrongPass"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }
}
