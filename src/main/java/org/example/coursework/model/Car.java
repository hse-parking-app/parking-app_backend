package org.example.coursework.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    UUID id;
    String model;
    Integer length;
    Integer wight;
    String registryNumber;

    public Car(String model, Integer length, Integer wight, String registryNumber) {
        this.id = UUID.randomUUID();
        this.model = model;
        this.length = length;
        this.wight = wight;
        this.registryNumber = registryNumber;
    }

    public Car() {
        this.id = UUID.randomUUID();
    }
}
