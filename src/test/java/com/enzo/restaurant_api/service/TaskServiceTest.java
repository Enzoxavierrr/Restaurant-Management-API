package com.enzo.restaurant_api.service;

import com.enzo.restaurant_api.entity.Task;
import com.enzo.restaurant_api.exception.TaskNotFoundException;
import com.enzo.restaurant_api.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(new InMemoryTaskRepository());
    }

    @Test
    void shouldCreateTask() {
        Task task = Task.builder().title("Limpar cozinha").description("Limpar a cozinha do restaurante").build();

        Task created = taskService.create(task);

        assertNotNull(created.getId());
        assertEquals("Limpar cozinha", created.getTitle());
        assertEquals("Limpar a cozinha do restaurante", created.getDescription());
        assertFalse(created.getCompleted());
    }

    @Test
    void shouldFindAllTasks() {
        taskService.create(Task.builder().title("Task 1").build());
        taskService.create(Task.builder().title("Task 2").build());

        List<Task> tasks = taskService.findAll();

        assertEquals(2, tasks.size());
    }

    @Test
    void shouldFindTaskById() {
        Task created = taskService.create(Task.builder().title("Minha task").build());

        Task found = taskService.findById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Minha task", found.getTitle());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskService.findById(999L));
    }

    @Test
    void shouldUpdateTask() {
        Task created = taskService.create(Task.builder().title("Original").build());

        Task updated = taskService.update(created.getId(),
                Task.builder().title("Atualizada").description("Nova descrição").build());

        assertEquals("Atualizada", updated.getTitle());
        assertEquals("Nova descrição", updated.getDescription());
    }

    @Test
    void shouldDeleteTask() {
        Task created = taskService.create(Task.builder().title("Para deletar").build());

        taskService.deleteById(created.getId());

        assertThrows(TaskNotFoundException.class, () -> taskService.findById(created.getId()));
    }

    @Test
    void shouldCompleteTask() {
        Task created = taskService.create(Task.builder().title("Para completar").build());

        Task completed = taskService.complete(created.getId());

        assertTrue(completed.getCompleted());
    }

    @Test
    void shouldUncompleteTask() {
        Task created = taskService.create(Task.builder().title("Para descompletar").completed(true).build());

        Task uncompleted = taskService.uncomplete(created.getId());

        assertFalse(uncompleted.getCompleted());
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.create(Task.builder().title("").build()));
    }

    @Test
    void shouldThrowWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.create(Task.builder().build()));
    }

    @Test
    void shouldThrowWhenTaskIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.create(null));
    }
}
