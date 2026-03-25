package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.dto.RestaurantRequest;
import com.enzo.restaurant_api.dto.RestaurantResponse;
import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.entity.User;
import com.enzo.restaurant_api.exception.DuplicateResourceException;
import com.enzo.restaurant_api.exception.RestaurantNotFoundException;
import com.enzo.restaurant_api.exception.UserNotFoundException;
import com.enzo.restaurant_api.repository.RestaurantRepository;
import com.enzo.restaurant_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantResponse create(RestaurantRequest request) {
        validateRequiredFields(request);

        if (restaurantRepository.existsByCnpj(request.getCnpj().trim())) {
            throw new DuplicateResourceException("Já existe um restaurante cadastrado com este CNPJ.");
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName().trim())
                .cnpj(request.getCnpj().trim())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(request.getActive() != null ? request.getActive() : true)
                .owner(owner)
                .build();
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse findByIdResponse(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> findAll() {
        return restaurantRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RestaurantResponse activate(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.activate();
        return toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse deactivate(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.deactivate();
        return toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest request) {
        validateRequiredFields(request);

        Restaurant existingRestaurant = findById(id);
        String normalizedCnpj = request.getCnpj().trim();
        if (restaurantRepository.existsByCnpjAndIdNot(normalizedCnpj, id)) {
            throw new DuplicateResourceException("Já existe um restaurante cadastrado com este CNPJ.");
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        existingRestaurant.setName(request.getName().trim());
        existingRestaurant.setCnpj(normalizedCnpj);
        existingRestaurant.setPhone(request.getPhone());
        existingRestaurant.setEmail(request.getEmail());
        existingRestaurant.setAddress(request.getAddress());
        existingRestaurant.setOwner(owner);

        if (request.getActive() != null) {
            existingRestaurant.setActive(request.getActive());
        }

        return toResponse(restaurantRepository.save(existingRestaurant));
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id);
        restaurantRepository.deleteById(id);
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .cnpj(restaurant.getCnpj())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .address(restaurant.getAddress())
                .active(restaurant.getActive())
                .build();
    }

    private void validateRequiredFields(RestaurantRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula");
        }

        if (isBlank(request.getName())) {
            throw new IllegalArgumentException(
                    "O campo 'name' (nome do restaurante) é obrigatório e não pode ser vazio.");
        }

        if (isBlank(request.getCnpj())) {
            throw new IllegalArgumentException("O campo 'cnpj' é obrigatório e não pode ser vazio.");
        }

        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("O campo 'ownerId' é obrigatório para registrar um restaurante.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
