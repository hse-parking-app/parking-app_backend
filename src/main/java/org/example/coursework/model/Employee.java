package org.example.coursework.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Employee {
    UUID id;
    String name;

    public Employee(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }
}
