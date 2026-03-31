package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.RegisterRequestDTO;
import com.enzo.restaurant_api.dto.RegisterResponseDTO;
import com.enzo.restaurant_api.dto.UserResponse;
import com.enzo.restaurant_api.entity.Role;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Requisição não pode ser nula.");

        String email = request.getEmail().trim().toLowerCase();
        if (userService.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .role(Role.OWNER)
                .build();

        User saved = userService.save(user);

        return RegisterResponseDTO.builder()
                .token(jwtService.generateToken(saved))
                .user(UserResponse.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .createdAt(saved.getCreatedAt())
                        .build())
                .build();
    }
}
