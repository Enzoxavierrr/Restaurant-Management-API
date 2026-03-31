package com.enzo.restaurant_api.security;

import com.enzo.restaurant_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@RequiredArgsConstructor
public class RepositoryUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.enzo.restaurant_api.entity.User user = userService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        List<SimpleGrantedAuthority> authorities = user.getRole() == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
