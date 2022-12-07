package org.example.coursework.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

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
