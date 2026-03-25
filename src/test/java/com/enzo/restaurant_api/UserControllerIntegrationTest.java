package com.enzo.restaurant_api;

import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldCreateUserAndPersistEncodedPassword() throws Exception {
        String payload = """
                {
                  "name": "Enzo",
                  "email": "enzo@example.com",
                  "password": "12345678"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("enzo@example.com"));

        User savedUser = userRepository.findByEmail("enzo@example.com").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("12345678");
        assertThat(passwordEncoder.matches("12345678", savedUser.getPassword())).isTrue();
    }

    @Test
    void shouldReturnConflictWhenCreatingUserWithDuplicateEmail() throws Exception {
        userRepository.save(User.builder()
                .name("Existing")
                .email("duplicate@example.com")
                .password(passwordEncoder.encode("12345678"))
                .createdAt(Instant.now())
                .role(com.enzo.restaurant_api.entity.Role.OWNER)
                .build());

        String payload = """
                {
                  "name": "Another",
                  "email": "duplicate@example.com",
                  "password": "12345678"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um usuário cadastrado com este e-mail."));
    }

    @Test
    void shouldRequireAuthenticationForRestaurantListing() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isUnauthorized());
    }
}
