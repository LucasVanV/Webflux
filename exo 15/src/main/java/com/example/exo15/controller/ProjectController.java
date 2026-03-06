package com.example.exo15.controller;

import com.example.exo15.model.Project;
import com.example.exo15.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public Flux<Project> getProjects(Authentication authentication) {
        String username = authentication.getName();
        return projectService.getProjectsByUsername(username);
    }
}