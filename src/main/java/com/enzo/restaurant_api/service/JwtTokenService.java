package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.config.JwtProperties;
import com.enzo.restaurant_api.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] secretBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuer("restaurant-api")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
