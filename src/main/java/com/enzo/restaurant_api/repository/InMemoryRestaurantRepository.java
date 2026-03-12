package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class InMemoryRestaurantRepository implements RestaurantRepository {

    private final Map<Long, Restaurant> database = new ConcurrentHashMap<>();

    @Override
    public Restaurant save(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            long randomId;
            do {
                randomId = ThreadLocalRandom.current().nextLong(100000L, 999999999L);
            } while (database.containsKey(randomId));
            restaurant.setId(randomId);
        }

        database.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void deleteById(Long id) {
        database.remove(id);
    }
}
