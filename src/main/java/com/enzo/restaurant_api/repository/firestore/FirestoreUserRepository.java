package com.enzo.restaurant_api.repository.firestore;

import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.firebase", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FirestoreUserRepository implements UserRepository {

    private static final String COLLECTION = "users";

    private final Firestore firestore;
    private final FirestoreSequenceService sequenceService;

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        DocumentSnapshot snapshot = FirestoreSupport.get(collection().document(id.toString()).get());
        return Optional.ofNullable(toUser(snapshot));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        List<QueryDocumentSnapshot> documents = FirestoreSupport.get(collection()
                .whereEqualTo("email", normalizeEmail(email))
                .limit(1)
                .get()).getDocuments();

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(toUser(documents.get(0)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        Long id = user.getId() != null ? user.getId() : sequenceService.nextId(COLLECTION);
        user.setId(id);
        FirestoreSupport.get(collection().document(id.toString()).set(toDocument(user)));
        return user;
    }

    @Override
    public List<User> findAll() {
        return FirestoreSupport.get(collection().get()).getDocuments().stream()
                .map(this::toUser)
                .filter(Objects::nonNull)
                .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
                .toList();
    }

    @Override
    public void deleteAll() {
        List<QueryDocumentSnapshot> documents = FirestoreSupport.get(collection().get()).getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            FirestoreSupport.get(document.getReference().delete());
        }
    }

    public Map<String, Object> toDocument(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", normalizeEmail(user.getEmail()));
        data.put("password", user.getPassword());
        data.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        data.put("role", user.getRole() != null ? user.getRole().name() : null);
        return data;
    }

    public User toUser(DocumentSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return null;
        }

        Long id = snapshot.getLong("id");
        String role = snapshot.getString("role");
        String createdAt = snapshot.getString("createdAt");

        return User.builder()
                .id(id != null ? id : Long.valueOf(snapshot.getId()))
                .name(snapshot.getString("name"))
                .email(snapshot.getString("email"))
                .password(snapshot.getString("password"))
                .createdAt(createdAt != null ? Instant.parse(createdAt) : null)
                .role(role != null ? Role.valueOf(role) : null)
                .build();
    }

    private CollectionReference collection() {
        return firestore.collection(COLLECTION);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
