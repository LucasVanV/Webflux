package com.example.exo.Exo9;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskBeans {

    @Bean
    TaskRepository taskRepository() {
        return new TaskRepository();
    }

    @Bean
    TaskService taskService(TaskRepository repo) {
        return new TaskService(repo);
    }
}