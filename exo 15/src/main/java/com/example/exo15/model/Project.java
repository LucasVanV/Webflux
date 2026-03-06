package com.example.exo15.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Table("projects")
@Getter
@Setter 
@AllArgsConstructor
public class Project {

    @Id
    private Long id;

    private String name;

    private String username;
}