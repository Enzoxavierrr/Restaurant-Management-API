package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.CreateUserRequest;
import com.enzo.restaurant_api.dto.UserResponse;
import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.exception.DuplicateResourceException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String COLLECTION = "users";

    private final Firestore db;
    private final PasswordEncoder passwordEncoder;

    // ── CRUD ────────────────────────────────────────────────

    public User save(User user) {
        try {
            if (user.getId() == null) user.setId(nextId());
            db.collection(COLLECTION).document(String.valueOf(user.getId())).set(toMap(user)).get();
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    public Optional<User> findById(Long id) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(String.valueOf(id)).get().get();
            return doc.exists() ? Optional.of(fromDoc(doc)) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário por id", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            return db.collection(COLLECTION).whereEqualTo("email", email).get().get()
                    .getDocuments().stream().findFirst().map(this::fromDoc);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        }
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public List<User> findAll() {
        try {
            return db.collection(COLLECTION).get().get()
                    .getDocuments().stream().map(this::fromDoc).toList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }
    }

    // ── API pública (usada pelo UserController) ──────────────

    public UserResponse create(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (existsByEmail(email)) {
            throw new DuplicateResourceException("Já existe um usuário com este e-mail.");
        }
        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build();
        return toResponse(save(user));
    }

    public List<UserResponse> listAll() {
        return findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────

    private Long nextId() throws Exception {
        return db.collection(COLLECTION).get().get().getDocuments().stream()
                .map(d -> d.getLong("id")).filter(id -> id != null)
                .mapToLong(Long::longValue).max().orElse(0L) + 1;
    }

    private java.util.Map<String, Object> toMap(User u) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", u.getId());
        map.put("name", u.getName());
        map.put("email", u.getEmail());
        map.put("password", u.getPassword());
        map.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        map.put("role", u.getRole() != null ? u.getRole().name() : null);
        return map;
    }

    private User fromDoc(DocumentSnapshot doc) {
        String createdAt = doc.getString("createdAt");
        String role = doc.getString("role");
        return User.builder()
                .id(doc.getLong("id"))
                .name(doc.getString("name"))
                .email(doc.getString("email"))
                .password(doc.getString("password"))
                .createdAt(createdAt != null ? Instant.parse(createdAt) : null)
                .role(role != null ? Role.valueOf(role) : null)
                .build();
    }
}
