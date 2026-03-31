package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.RestaurantRequest;
import com.enzo.restaurant_api.dto.RestaurantResponse;
import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.exception.DuplicateResourceException;
import com.enzo.restaurant_api.exception.RestaurantNotFoundException;
import com.enzo.restaurant_api.exception.UserNotFoundException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private static final String COLLECTION = "restaurants";

    private final Firestore db;
    private final UserService userService;

    // ── CRUD ────────────────────────────────────────────────

    public RestaurantResponse create(RestaurantRequest req) {
        if (isBlank(req.getName())) throw new IllegalArgumentException("O campo 'name' é obrigatório.");
        if (isBlank(req.getCnpj())) throw new IllegalArgumentException("O campo 'cnpj' é obrigatório.");
        if (req.getOwnerId() == null) throw new IllegalArgumentException("O campo 'ownerId' é obrigatório.");

        String cnpj = req.getCnpj().trim();
        if (existsByCnpj(cnpj)) throw new DuplicateResourceException("CNPJ já cadastrado.");

        userService.findById(req.getOwnerId()).orElseThrow(() -> new UserNotFoundException(req.getOwnerId()));

        Restaurant r = Restaurant.builder()
                .name(req.getName().trim()).cnpj(cnpj)
                .phone(req.getPhone()).email(req.getEmail()).address(req.getAddress())
                .active(req.getActive() != null ? req.getActive() : true)
                .ownerId(req.getOwnerId())
                .build();
        return toResponse(save(r));
    }

    public RestaurantResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public List<RestaurantResponse> getAll() {
        return findAll().stream().map(this::toResponse).toList();
    }

    public RestaurantResponse update(Long id, RestaurantRequest req) {
        if (isBlank(req.getName())) throw new IllegalArgumentException("O campo 'name' é obrigatório.");
        if (isBlank(req.getCnpj())) throw new IllegalArgumentException("O campo 'cnpj' é obrigatório.");
        if (req.getOwnerId() == null) throw new IllegalArgumentException("O campo 'ownerId' é obrigatório.");

        Restaurant existing = findById(id);
        String cnpj = req.getCnpj().trim();
        if (existsByCnpjAndIdNot(cnpj, id)) throw new DuplicateResourceException("CNPJ já cadastrado.");

        userService.findById(req.getOwnerId()).orElseThrow(() -> new UserNotFoundException(req.getOwnerId()));

        existing.setName(req.getName().trim());
        existing.setCnpj(cnpj);
        existing.setPhone(req.getPhone());
        existing.setEmail(req.getEmail());
        existing.setAddress(req.getAddress());
        existing.setOwnerId(req.getOwnerId());
        if (req.getActive() != null) existing.setActive(req.getActive());

        return toResponse(save(existing));
    }

    public RestaurantResponse activate(Long id) {
        Restaurant r = findById(id);
        r.activate();
        return toResponse(save(r));
    }

    public RestaurantResponse deactivate(Long id) {
        Restaurant r = findById(id);
        r.deactivate();
        return toResponse(save(r));
    }

    public void deleteById(Long id) {
        findById(id); // garante que existe
        try {
            db.collection(COLLECTION).document(String.valueOf(id)).delete().get();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar restaurante", e);
        }
    }

    // ── Helpers internos ─────────────────────────────────────

    private Restaurant save(Restaurant r) {
        try {
            if (r.getId() == null) r.setId(nextId());
            db.collection(COLLECTION).document(String.valueOf(r.getId())).set(toMap(r)).get();
            return r;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar restaurante", e);
        }
    }

    private Restaurant findById(Long id) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(String.valueOf(id)).get().get();
            if (!doc.exists()) throw new RestaurantNotFoundException(id);
            return fromDoc(doc);
        } catch (RestaurantNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar restaurante", e);
        }
    }

    private List<Restaurant> findAll() {
        try {
            return db.collection(COLLECTION).get().get()
                    .getDocuments().stream().map(this::fromDoc).toList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar restaurantes", e);
        }
    }

    private Optional<Restaurant> findByCnpj(String cnpj) {
        try {
            return db.collection(COLLECTION).whereEqualTo("cnpj", cnpj).get().get()
                    .getDocuments().stream().findFirst().map(this::fromDoc);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar por CNPJ", e);
        }
    }

    private boolean existsByCnpj(String cnpj) {
        return findByCnpj(cnpj).isPresent();
    }

    private boolean existsByCnpjAndIdNot(String cnpj, Long id) {
        return findByCnpj(cnpj).map(r -> !r.getId().equals(id)).orElse(false);
    }

    private Long nextId() throws Exception {
        return db.collection(COLLECTION).get().get().getDocuments().stream()
                .map(d -> d.getLong("id")).filter(id -> id != null)
                .mapToLong(Long::longValue).max().orElse(0L) + 1;
    }

    private Map<String, Object> toMap(Restaurant r) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", r.getId());
        map.put("name", r.getName());
        map.put("cnpj", r.getCnpj());
        map.put("phone", r.getPhone());
        map.put("email", r.getEmail());
        map.put("address", r.getAddress());
        map.put("active", r.getActive());
        map.put("ownerId", r.getOwnerId());
        return map;
    }

    private Restaurant fromDoc(DocumentSnapshot doc) {
        return Restaurant.builder()
                .id(doc.getLong("id")).name(doc.getString("name")).cnpj(doc.getString("cnpj"))
                .phone(doc.getString("phone")).email(doc.getString("email"))
                .address(doc.getString("address")).active(doc.getBoolean("active"))
                .ownerId(doc.getLong("ownerId"))
                .build();
    }

    private RestaurantResponse toResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId()).name(r.getName()).cnpj(r.getCnpj())
                .phone(r.getPhone()).email(r.getEmail()).address(r.getAddress())
                .active(r.getActive())
                .build();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
