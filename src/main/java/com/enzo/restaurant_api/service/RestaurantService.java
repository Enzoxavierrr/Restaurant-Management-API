package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.exception.RestaurantNotFoundException;
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

        if (restaurant.getActive() == null) {
            restaurant.activate();
        }

        return restaurantRepository.save(restaurant);
    }

    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
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

    public Restaurant update(Long id, Restaurant restaurant) {
        validateRequiredFields(restaurant);

        Restaurant existingRestaurant = findById(id);
        existingRestaurant.setName(restaurant.getName());
        existingRestaurant.setCnpj(restaurant.getCnpj());
        existingRestaurant.setPhone(restaurant.getPhone());
        existingRestaurant.setEmail(restaurant.getEmail());
        existingRestaurant.setAddress(restaurant.getAddress());

        if (restaurant.getActive() != null) {
            existingRestaurant.setActive(restaurant.getActive());
        }

        return restaurantRepository.save(existingRestaurant);
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
            throw new IllegalArgumentException(
                    "O campo 'name' (nome do restaurante) é obrigatório e não pode ser vazio.");
        }

        if (isBlank(restaurant.getCnpj())) {
            throw new IllegalArgumentException("O campo 'cnpj' é obrigatório e não pode ser vazio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
