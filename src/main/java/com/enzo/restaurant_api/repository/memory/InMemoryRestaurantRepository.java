package com.enzo.restaurant_api.repository.memory;

import com.enzo.restaurant_api.entity.Restaurant;
import com.enzo.restaurant_api.repository.RestaurantRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
@Profile("test")
public class InMemoryRestaurantRepository implements RestaurantRepository {

    private final ConcurrentHashMap<Long, Restaurant> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Optional<Restaurant> findById(Long id) {
        return Optional.ofNullable(copy(storage.get(id)));
    }

    @Override
    public Optional<Restaurant> findByCnpj(String cnpj) {
        if (cnpj == null) {
            return Optional.empty();
        }

        String normalizedCnpj = cnpj.trim();
        return storage.values().stream()
                .filter(restaurant -> normalizedCnpj.equals(restaurant.getCnpj()))
                .findFirst()
                .map(this::copy);
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return findByCnpj(cnpj).isPresent();
    }

    @Override
    public boolean existsByCnpjAndIdNot(String cnpj, Long id) {
        return findByCnpj(cnpj)
                .map(restaurant -> !restaurant.getId().equals(id))
                .orElse(false);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        Restaurant copy = copy(restaurant);
        if (copy.getId() == null) {
            copy.setId(sequence.incrementAndGet());
        }
        storage.put(copy.getId(), copy);
        return copy(copy);
    }

    @Override
    public List<Restaurant> findAll() {
        return storage.values().stream()
                .map(this::copy)
                .sorted(Comparator.comparing(Restaurant::getId))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    private Restaurant copy(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        return Restaurant.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .cnpj(restaurant.getCnpj())
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .address(restaurant.getAddress())
                .active(restaurant.getActive())
                .owner(restaurant.getOwner())
                .build();
    }
}
