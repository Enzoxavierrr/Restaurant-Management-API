package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    List<Restaurant> findAll();

    void deleteById(Long id);
}
