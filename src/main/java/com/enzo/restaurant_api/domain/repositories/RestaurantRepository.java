package com.enzo.restaurant_api.domain.repositories;

import com.enzo.restaurant_api.domain.entities.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    List<Restaurant> findAll();

    void deleteById(Long id);
}