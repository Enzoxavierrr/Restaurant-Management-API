package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.CreateUserRequest;
import com.enzo.restaurant_api.dto.UserResponse;
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

    public UserResponse create(CreateUserRequest request) {
        validateRequiredFields(request);
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .createdAt(Instant.now())
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

        if (isBlank(request.getName())) {
            throw new IllegalArgumentException("O campo 'name' é obrigatório e não pode ser vazio.");
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("O campo 'email' é obrigatório e não pode ser vazio.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("O campo 'password' é obrigatório e não pode ser vazio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}