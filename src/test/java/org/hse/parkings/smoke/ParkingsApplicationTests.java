package org.hse.parkings.smoke;

import org.hse.parkings.AbstractTest;
import org.hse.parkings.controller.CarController;
import org.hse.parkings.controller.EmployeeController;
import org.hse.parkings.controller.ReservationController;
import org.hse.parkings.controller.building.BuildingController;
import org.hse.parkings.controller.building.ParkingLevelController;
import org.hse.parkings.controller.building.ParkingSpotController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Smoke")
class ParkingsApplicationTests extends AbstractTest {

    @Autowired
    private CarController carController;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private BuildingController buildingController;
    @Autowired
    private ParkingLevelController parkingLevelController;
    @Autowired
    private ParkingSpotController parkingSpotController;

    @Autowired
    private ReservationController reservationController;

    @Test
    @DisplayName("Smoke")
    void contextLoads() {
        assertThat(carController).isNotNull();
        assertThat(employeeController).isNotNull();
        assertThat(buildingController).isNotNull();
        assertThat(parkingLevelController).isNotNull();
        assertThat(parkingSpotController).isNotNull();
        assertThat(reservationController).isNotNull();
    }
}
