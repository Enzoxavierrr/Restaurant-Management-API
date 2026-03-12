package com.enzo.restaurant_api.controller;

import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public List<Restaurant> findAll() {
        return restaurantService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody Restaurant restaurant) {
        return restaurantService.create(restaurant);
    }

    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        return restaurantService.update(id, restaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteById(@PathVariable Long id) {
        restaurantService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Restaurante deletado com sucesso"));
    }
}
