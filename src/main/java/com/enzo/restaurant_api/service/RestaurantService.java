package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant create(Restaurant restaurant) {
        validateRequiredFields(restaurant);
        restaurant.activate();
        return restaurantRepository.save(restaurant);
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant não encontrado para o id: " + id));
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant activate(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.activate();
        return restaurantRepository.save(restaurant);
    }

    public Restaurant deactivate(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.deactivate();
        return restaurantRepository.save(restaurant);
    }

    public void deleteById(Long id) {
        findById(id);
        restaurantRepository.deleteById(id);
    }

    private void validateRequiredFields(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant não pode ser nulo");
        }

        if (isBlank(restaurant.getName())) {
            throw new IllegalArgumentException("Nome do restaurant é obrigatório");
        }

        if (isBlank(restaurant.getCnpj())) {
            throw new IllegalArgumentException("CNPJ do restaurant é obrigatório");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
