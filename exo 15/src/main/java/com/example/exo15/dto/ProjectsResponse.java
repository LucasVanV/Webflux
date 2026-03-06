package com.example.exo15.dto;

import java.util.List;

public class ProjectsResponse {

    private List<String> projects;

    public ProjectsResponse() {
    }

    public ProjectsResponse(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }
}