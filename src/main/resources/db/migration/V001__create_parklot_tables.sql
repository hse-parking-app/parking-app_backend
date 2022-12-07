CREATE TABLE cars
(
    id UUID PRIMARY KEY,
    model VARCHAR NOT NULL,
    dimension_length INTEGER NOT NULL,
    dimension_wight INTEGER NOT NULL,
    registry_number VARCHAR NOT NULL
);

CREATE TABLE employees
(
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE parking_spots
(
    id UUID PRIMARY KEY,
    parking_number VARCHAR NOT NULL,
    is_free BOOLEAN NOT NULL
);

CREATE TABLE reservations
(
    id UUID PRIMARY KEY,
    car_id UUID NOT NULL,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    parking_spot_id UUID NOT NULL,
    FOREIGN KEY (parking_spot_id) REFERENCES parking_spots(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
)

