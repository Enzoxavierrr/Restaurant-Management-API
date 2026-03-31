package com.enzo.restaurant_api.controller;

import com.enzo.restaurant_api.dto.LoginRequestDTO;
import com.enzo.restaurant_api.dto.LoginResponseDTO;
import com.enzo.restaurant_api.dto.RegisterRequestDTO;
import com.enzo.restaurant_api.dto.RegisterResponseDTO;
import com.enzo.restaurant_api.exception.InvalidCredentialsException;
import com.enzo.restaurant_api.security.JwtService;
import com.enzo.restaurant_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail().trim().toLowerCase(),
                request.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            String token = jwtService.generateToken(authentication);
            return LoginResponseDTO.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .build();
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }
}
