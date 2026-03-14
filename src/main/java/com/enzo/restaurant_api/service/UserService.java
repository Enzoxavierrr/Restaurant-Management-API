package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        validateRequiredFields(user);
        user.setCreatedAt(Instant.now());
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private void validateRequiredFields(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User não pode ser nulo");
        }

        if (isBlank(user.getName())) {
            throw new IllegalArgumentException("O campo 'name' é obrigatório e não pode ser vazio.");
        }

        if (isBlank(user.getEmail())) {
            throw new IllegalArgumentException("O campo 'email' é obrigatório e não pode ser vazio.");
        }

        if (isBlank(user.getPassword())) {
            throw new IllegalArgumentException("O campo 'password' é obrigatório e não pode ser vazio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}