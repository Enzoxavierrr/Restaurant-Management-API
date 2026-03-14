package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    List<User> findAll();
}