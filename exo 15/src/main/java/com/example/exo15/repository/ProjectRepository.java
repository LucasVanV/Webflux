package com.example.exo15.repository;

import com.example.exo15.model.Project;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long> {

    Flux<Project> findByUsername(String username);
}