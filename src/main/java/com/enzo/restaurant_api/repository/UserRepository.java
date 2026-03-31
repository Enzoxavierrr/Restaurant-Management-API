package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);

    List<User> findAll();

    void deleteAll();
}
