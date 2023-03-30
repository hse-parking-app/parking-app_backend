CREATE TABLE employees
(
    id       UUID PRIMARY KEY,
    name     VARCHAR        NOT NULL,
    email    VARCHAR UNIQUE NOT NULL,
    password VARCHAR        NOT NULL
);

CREATE TABLE cars
(
    id              UUID PRIMARY KEY,
    owner_id        UUID    NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES employees (id) ON DELETE CASCADE,
    model           VARCHAR NOT NULL,
    length_meters   FLOAT,
    weight_tons     FLOAT,
    registry_number VARCHAR NOT NULL
);

CREATE TABLE buildings
(
    id               UUID PRIMARY KEY,
    name             VARCHAR NOT NULL,
    address          VARCHAR NOT NULL,
    number_of_levels INTEGER NOT NULL
);

CREATE TYPE integer_pair AS (first INTEGER, second INTEGER);

CREATE TABLE parking_levels
(
    id              UUID PRIMARY KEY,
    building_id     UUID         NOT NULL,
    FOREIGN KEY (building_id) REFERENCES buildings (id) ON DELETE CASCADE,
    layer_name      VARCHAR      NOT NULL,
    number_of_spots INTEGER      NOT NULL,
    canvas          integer_pair NOT NULL
);

CREATE TABLE parking_spots
(
    id               UUID PRIMARY KEY,
    level_id         UUID         NOT NULL,
    FOREIGN KEY (level_id) REFERENCES parking_levels (id) ON DELETE CASCADE,
    building_id      UUID         NOT NULL,
    FOREIGN KEY (building_id) REFERENCES buildings (id) ON DELETE CASCADE,
    parking_number   VARCHAR      NOT NULL,
    is_available     BOOLEAN      NOT NULL,
    is_free          BOOLEAN      NOT NULL,
    canvas           integer_pair NOT NULL,
    on_canvas_coords integer_pair NOT NULL
);

CREATE TABLE reservations
(
    id          UUID PRIMARY KEY,
    car_id      UUID      NOT NULL,
    FOREIGN KEY (car_id) REFERENCES cars (id) ON DELETE CASCADE,
    employee_id UUID      NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    spot_id     UUID      NOT NULL,
    FOREIGN KEY (spot_id) REFERENCES parking_spots (id) ON DELETE CASCADE,
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL
);
