package com.enzo.restaurant_api.repository.firestore;

import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.RestaurantRepository;
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
public class FirestoreRestaurantRepository implements RestaurantRepository {

    private static final String COLLECTION = "restaurants";

    private final Firestore firestore;
    private final FirestoreSequenceService sequenceService;

    @Override
    public Optional<Restaurant> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        DocumentSnapshot snapshot = FirestoreSupport.get(collection().document(id.toString()).get());
        return Optional.ofNullable(toRestaurant(snapshot));
    }

    @Override
    public Optional<Restaurant> findByCnpj(String cnpj) {
        if (cnpj == null) {
            return Optional.empty();
        }

        List<QueryDocumentSnapshot> documents = FirestoreSupport.get(collection()
                .whereEqualTo("cnpj", normalizeCnpj(cnpj))
                .limit(1)
                .get()).getDocuments();

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(toRestaurant(documents.get(0)));
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return findByCnpj(cnpj).isPresent();
    }

    @Override
    public boolean existsByCnpjAndIdNot(String cnpj, Long id) {
        return findByCnpj(cnpj)
                .map(restaurant -> !restaurant.getId().equals(id))
                .orElse(false);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        Long id = restaurant.getId() != null ? restaurant.getId() : sequenceService.nextId(COLLECTION);
        restaurant.setId(id);
        FirestoreSupport.get(collection().document(id.toString()).set(toDocument(restaurant)));
        return restaurant;
    }

    @Override
    public List<Restaurant> findAll() {
        return FirestoreSupport.get(collection().get()).getDocuments().stream()
                .map(this::toRestaurant)
                .filter(Objects::nonNull)
                .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }
        FirestoreSupport.get(collection().document(id.toString()).delete());
    }

    private Map<String, Object> toDocument(Restaurant restaurant) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", restaurant.getId());
        data.put("name", restaurant.getName());
        data.put("cnpj", normalizeCnpj(restaurant.getCnpj()));
        data.put("phone", restaurant.getPhone());
        data.put("email", restaurant.getEmail());
        data.put("address", restaurant.getAddress());
        data.put("active", restaurant.getActive());
        data.put("owner", toOwnerDocument(restaurant.getOwner()));
        return data;
    }

    private Map<String, Object> toOwnerDocument(User owner) {
        if (owner == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", owner.getId());
        data.put("name", owner.getName());
        data.put("email", owner.getEmail());
        data.put("password", owner.getPassword());
        data.put("createdAt", owner.getCreatedAt() != null ? owner.getCreatedAt().toString() : null);
        data.put("role", owner.getRole() != null ? owner.getRole().name() : null);
        return data;
    }

    private Restaurant toRestaurant(DocumentSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return null;
        }

        Long id = snapshot.getLong("id");
        Boolean active = snapshot.getBoolean("active");

        return Restaurant.builder()
                .id(id != null ? id : Long.valueOf(snapshot.getId()))
                .name(snapshot.getString("name"))
                .cnpj(snapshot.getString("cnpj"))
                .phone(snapshot.getString("phone"))
                .email(snapshot.getString("email"))
                .address(snapshot.getString("address"))
                .active(active != null ? active : Boolean.TRUE)
                .owner(toOwner(snapshot.get("owner")))
                .build();
    }

    @SuppressWarnings("unchecked")
    private User toOwner(Object value) {
        if (!(value instanceof Map<?, ?> ownerMap)) {
            return null;
        }

        Object id = ownerMap.get("id");
        Object createdAt = ownerMap.get("createdAt");
        Object role = ownerMap.get("role");

        return User.builder()
                .id(id instanceof Number number ? number.longValue() : null)
                .name((String) ownerMap.get("name"))
                .email((String) ownerMap.get("email"))
                .password((String) ownerMap.get("password"))
                .createdAt(createdAt instanceof String text ? Instant.parse(text) : null)
                .role(role instanceof String text ? com.enzo.restaurant_api.entity.Role.valueOf(text) : null)
                .build();
    }

    private CollectionReference collection() {
        return firestore.collection(COLLECTION);
    }

    private String normalizeCnpj(String cnpj) {
        return cnpj.trim();
    }
}
