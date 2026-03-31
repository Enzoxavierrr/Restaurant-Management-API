package com.enzo.restaurant_api.controller;

import com.enzo.restaurant_api.dto.RestaurantRequest;
import com.enzo.restaurant_api.dto.RestaurantResponse;
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
import jakarta.validation.Valid;
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
    public List<RestaurantResponse> findAll() {
        return restaurantService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable Long id, @Valid @RequestBody RestaurantRequest request) {
        return restaurantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteById(@PathVariable Long id) {
        restaurantService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Restaurante deletado com sucesso"));
    }
}
