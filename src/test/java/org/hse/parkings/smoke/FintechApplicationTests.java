package org.hse.parkings.smoke;

import org.hse.parkings.controller.CarController;
import org.hse.parkings.controller.EmployeeController;
import org.hse.parkings.controller.ParkingSpotController;
import org.hse.parkings.controller.ReservationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FintechApplicationTests {

    @Autowired
    private CarController carController;
    @Autowired
    private EmployeeController employeeController;
    @Autowired
    private ParkingSpotController parkingSpotController;
    @Autowired
    private ReservationController reservationController;

    @Test
    void contextLoads() {
        assertThat(carController).isNotNull();
        assertThat(employeeController).isNotNull();
        assertThat(parkingSpotController).isNotNull();
        assertThat(reservationController).isNotNull();
    }

}
