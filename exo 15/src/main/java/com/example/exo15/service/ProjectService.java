package com.example.exo15.service;

import com.example.exo15.model.Project;
import com.example.exo15.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Flux<Project> getProjectsByUsername(String username) {
        return projectRepository.findByUsername(username);
    }
}