package com.example.exo.Exo9;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TaskRepository {
    
    private final Map<Integer, Task> tasks = new HashMap<>();
    private int taskIdCounter = 0;

    public TaskRepository() {

    }

    /**
     * Add a new task to the repository.
     * @param task the task to add
     * @return the ID of the newly added task
     */
    public long addTask(Task task) {
        int id = ++taskIdCounter;
        tasks.put(id, task);
        return id;
    }

    /**
     * Get a task by ID.
     * @param id the task ID
     * @return Optional containing the task if found, empty otherwise
     */
    public Optional<Task> getTask(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Update an existing task.
     * @param id the task ID
     * @param updatedTask the updated task data
     * @return true if the task was updated, false if not found
     */
    public boolean updateTask(int id, Task updatedTask) {
        if (tasks.containsKey(id)) {
            tasks.put(id, updatedTask);
            return true;
        }
        return false;
    }

    /**
     * Delete a task by ID.
     * @param id the task ID
     * @return true if the task was deleted, false if not found
     */
    public boolean deleteTask(int id) {
        return tasks.remove(id) != null;
    }

    /**
     * Get all tasks.
     * @return a copy of the tasks map
     */
    public Map<Integer, Task> getAllTasks() {
        return new HashMap<>(tasks);
    }

    /**
     * Check if a task exists.
     * @param id the task ID
     * @return true if the task exists, false otherwise
     */
    public boolean taskExists(int id) {
        return tasks.containsKey(id);
    }
}
