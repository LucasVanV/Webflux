package com.example.exo.Exo9;

import com.example.exo.Exo9.dto.TaskResponse;
import com.example.exo.Exo9.dto.TaskUpdateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public Flux<TaskResponse> getAll() {
        return Flux.fromIterable(repo.getAllTasks().entrySet())
                .map(e -> TaskResponse.from(e.getKey(), e.getValue()));
    }

    public Mono<TaskResponse> getById(int id) {
        return Mono.justOrEmpty(repo.getTask(id))
                .map(t -> TaskResponse.from(id, t));
    }

    public Mono<TaskResponse> create(Task task) {
        return Mono.fromSupplier(() -> {
            int id = (int) repo.addTask(task);
            Task saved = repo.getTask(id).orElse(task);
            return TaskResponse.from(id, saved);
        });
    }

    public Mono<TaskResponse> update(int id, TaskUpdateRequest req) {
        return Mono.fromSupplier(() -> repo.getTask(id))
                .flatMap(opt -> Mono.justOrEmpty(opt))
                .map(existing -> {
                    String newDesc = req.description() != null ? req.description() : existing.getDescription();
                    boolean newDone = req.done() != null ? req.done() : existing.isCompleted();

                    Task updated = new Task(existing.getTitle(), newDesc, newDone);
                    repo.updateTask(id, updated);
                    return TaskResponse.from(id, updated);
                });
    }

    public Mono<Boolean> delete(int id) {
        return Mono.fromSupplier(() -> repo.deleteTask(id));
    }
}