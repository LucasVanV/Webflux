package com.example.exo.Exo9;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.exo.Exo9.dto.TaskResponse;
import com.example.exo.Exo9.dto.TaskUpdateRequest;

import reactor.core.publisher.Mono;

@Component
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAll(), TaskResponse.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        return service.getById(id)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Task.class)
                .flatMap(service::create)
                .flatMap(created ->
                        ServerResponse.created(request.uriBuilder()
                                        .path("/{id}")
                                        .build(created.id()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(created));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));

        return request.bodyToMono(TaskUpdateRequest.class)
                .flatMap(req -> service.update(id, req))
                .flatMap(updated -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updated))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        int id = Integer.parseInt(request.pathVariable("id"));
        return service.delete(id)
                .flatMap(deleted -> deleted
                        ? ServerResponse.noContent().build()
                        : ServerResponse.notFound().build());
    }
}