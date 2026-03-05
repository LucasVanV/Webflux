package com.example.exo.Exo9;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Task {
    private String title;
    private String description;
    private boolean isCompleted;
}
