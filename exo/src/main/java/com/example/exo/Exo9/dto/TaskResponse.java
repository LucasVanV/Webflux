package com.example.exo.Exo9.dto;

import com.example.exo.Exo9.Task;

public record TaskResponse(int id, String title, String description, boolean done) {
    public static TaskResponse from(int id, Task t) {
        return new TaskResponse(id, t.getTitle(), t.getDescription(), t.isCompleted());
    }
}