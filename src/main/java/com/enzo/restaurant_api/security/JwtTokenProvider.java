package com.enzo.restaurant_api.security;

import com.enzo.restaurant_api.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException("A propriedade security.jwt.secret deve estar configurada.");
        }

        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getExpiration());

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
}
