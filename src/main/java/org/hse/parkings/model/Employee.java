package org.hse.parkings.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Employee {
    @Builder.Default
    UUID id = UUID.randomUUID();
    @NonNull
    String name;
}
