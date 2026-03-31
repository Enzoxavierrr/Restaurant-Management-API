package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.CreateUserRequest;
import com.enzo.restaurant_api.dto.UserResponse;
import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.exception.DuplicateResourceException;
import com.enzo.restaurant_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(CreateUserRequest request) {
        validateRequiredFields(request);

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Já existe um usuário cadastrado com este e-mail.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build();

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private void validateRequiredFields(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula");
        }
    }
}
