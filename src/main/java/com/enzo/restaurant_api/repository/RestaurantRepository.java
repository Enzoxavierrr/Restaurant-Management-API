package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    Restaurant save(Restaurant restaurant);

    List<Restaurant> findAll();

    void deleteById(Long id);
}
