package org.example.coursework.smoke;

import org.example.coursework.controller.CarController;
import org.example.coursework.controller.EmployeeController;
import org.example.coursework.controller.ParkingSpotController;
import org.example.coursework.controller.ReservationController;
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
