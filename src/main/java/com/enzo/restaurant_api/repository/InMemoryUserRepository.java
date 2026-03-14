package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> database = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            long randomId;
            do {
                randomId = ThreadLocalRandom.current().nextLong(100000L, 999999999L);
            } while (database.containsKey(randomId));
            user.setId(randomId);
        }

        database.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }
}