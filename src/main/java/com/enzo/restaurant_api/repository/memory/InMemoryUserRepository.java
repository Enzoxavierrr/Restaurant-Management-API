package com.enzo.restaurant_api.repository.memory;

import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
@Profile("test")
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        String normalizedEmail = email.trim().toLowerCase();
        return storage.values().stream()
                .filter(user -> normalizedEmail.equals(user.getEmail()))
                .findFirst()
                .map(this::copy);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        User copy = copy(user);
        if (copy.getId() == null) {
            copy.setId(sequence.incrementAndGet());
        }
        storage.put(copy.getId(), copy);
        return copy(copy);
    }

    @Override
    public List<User> findAll() {
        return storage.values().stream()
                .map(this::copy)
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    @Override
    public void deleteAll() {
        storage.clear();
        sequence.set(0L);
    }

    private User copy(User user) {
        if (user == null) {
            return null;
        }

        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();
    }
}
