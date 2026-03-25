package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.RegisterRequestDTO;
import com.enzo.restaurant_api.dto.RegisterResponseDTO;
import com.enzo.restaurant_api.dto.UserResponse;
import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula.");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponseDTO.builder()
                .token(jwtTokenService.generateToken(savedUser))
                .user(toUserResponse(savedUser))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
