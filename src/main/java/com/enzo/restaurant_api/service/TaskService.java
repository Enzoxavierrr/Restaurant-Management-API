package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.entity.Task;
import com.enzo.restaurant_api.exception.TaskNotFoundException;
import com.enzo.restaurant_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task create(Task task) {
        validateRequiredFields(task);

        if (task.getCompleted() == null) {
            task.uncomplete();
        }

        return taskRepository.save(task);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task complete(Long id) {
        Task task = findById(id);
        task.complete();
        return taskRepository.save(task);
    }

    public Task uncomplete(Long id) {
        Task task = findById(id);
        task.uncomplete();
        return taskRepository.save(task);
    }

    public Task update(Long id, Task task) {
        validateRequiredFields(task);

        Task existingTask = findById(id);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());

        if (task.getCompleted() != null) {
            existingTask.setCompleted(task.getCompleted());
        }

        return taskRepository.save(existingTask);
    }

    public void deleteById(Long id) {
        findById(id);
        taskRepository.deleteById(id);
    }

    private void validateRequiredFields(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser nula");
        }

        if (isBlank(task.getTitle())) {
            throw new IllegalArgumentException(
                    "O campo 'title' (título da task) é obrigatório e não pode ser vazio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
