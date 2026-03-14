package com.enzo.restaurant_api.repository;

import com.enzo.restaurant_api.entity.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> database = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            long randomId;
            do {
                randomId = ThreadLocalRandom.current().nextLong(100000L, 999999999L);
            } while (database.containsKey(randomId));
            task.setId(randomId);
        }

        database.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void deleteById(Long id) {
        database.remove(id);
    }
}
