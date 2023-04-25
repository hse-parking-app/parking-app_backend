package org.hse.parkings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import org.hse.parkings.filter.JwtHeaderFilter;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.building.*;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.utils.DateTimeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Collections;

import static org.hse.parkings.utils.Cache.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractTest {

    protected final Employee employeeAlice = Employee.builder()
            .name("Alice")
            .email("aliceeee@o.o")
            .password("123")
            .roles(Collections.singleton(Role.ADMIN)).build();
    protected final Employee employeeBob = Employee.builder()
            .name("Bob")
            .email("goodbob@123.wow")
            .password("123")
            .roles(Collections.singleton(Role.ADMIN)).build();

    protected final Car carSupraOfAlice = Car.builder()
            .ownerId(employeeAlice.getId())
            .model("Supra")
            .lengthMeters(1.43)
            .weightTons(1.3333)
            .registryNumber("aa777a152").build();
    protected final Car carSkylineOfAlice = Car.builder()
            .ownerId(employeeAlice.getId())
            .model("Skyline")
            .lengthMeters(1.43)
            .weightTons(1.3333)
            .registryNumber("aa733b152").build();
    protected final Car carAudiOfBob = Car.builder()
            .ownerId(employeeBob.getId())
            .model("Audi R8")
            .lengthMeters(1.5)
            .weightTons(1.1)
            .registryNumber("ABC-123").build();
    protected final Car carTeslaOfBob = Car.builder()
            .ownerId(employeeBob.getId())
            .model("Tesla Model X")
            .lengthMeters(1.5)
            .weightTons(1.1)
            .registryNumber("ABC-125").build();

    protected final Building building = Building.builder()
            .name("Building")
            .address("Street")
            .numberOfLevels(1).build();
    protected final ParkingLevel parkingLevelOne = ParkingLevel.builder()
            .buildingId(building.getId())
            .layerName("Level 1")
            .numberOfSpots(3)
            .canvas(new CanvasSize(1, 1)).build();
    protected final ParkingSpot parkingSpotA = ParkingSpot.builder()
            .levelId(parkingLevelOne.getId())
            .buildingId(building.getId())
            .parkingNumber("A")
            .isAvailable(true)
            .isFree(true)
            .canvas(new CanvasSize(1, 1))
            .onCanvasCoords(new OnCanvasCoords(1, 1)).build();
    protected final ParkingSpot parkingSpotB = ParkingSpot.builder()
            .levelId(parkingLevelOne.getId())
            .buildingId(building.getId())
            .parkingNumber("B")
            .isAvailable(true)
            .isFree(true)
            .canvas(new CanvasSize(1, 1))
            .onCanvasCoords(new OnCanvasCoords(1, 1)).build();
    protected final ParkingSpot parkingSpotC = ParkingSpot.builder()
            .levelId(parkingLevelOne.getId())
            .buildingId(building.getId())
            .parkingNumber("C")
            .isAvailable(false)
            .isFree(true)
            .canvas(new CanvasSize(1, 1))
            .onCanvasCoords(new OnCanvasCoords(1, 1)).build();
    protected final ParkingLevel parkingLevelTwo = ParkingLevel.builder()
            .buildingId(building.getId())
            .layerName("Level 2")
            .numberOfSpots(3)
            .canvas(new CanvasSize(1, 1)).build();
    protected final ParkingSpot parkingSpotD = ParkingSpot.builder()
            .levelId(parkingLevelTwo.getId())
            .buildingId(building.getId())
            .parkingNumber("D")
            .isAvailable(true)
            .isFree(true)
            .canvas(new CanvasSize(1, 1))
            .onCanvasCoords(new OnCanvasCoords(1, 1)).build();
    protected final ParkingSpot parkingSpotE = ParkingSpot.builder()
            .levelId(parkingLevelTwo.getId())
            .buildingId(building.getId())
            .parkingNumber("E")
            .isAvailable(true)
            .isFree(true)
            .canvas(new CanvasSize(1, 1))
            .onCanvasCoords(new OnCanvasCoords(1, 1)).build();

    protected final DateTimeProvider dateTimeProvider = DateTimeProvider.getInstance();

    protected String adminToken = null;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PasswordEncoder encoder;

    protected ObjectMapper jackson = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected void adjustClockTo(DayOfWeek day) {
        DayOfWeek dayOfWeek = dateTimeProvider.getZonedDateTime().getDayOfWeek();
        int i = 0;
        dayOfWeek = dayOfWeek.plus(++i);
        while (dayOfWeek != day) {
            i++;
            dayOfWeek = dayOfWeek.plus(1);
        }
        dateTimeProvider.offsetClock(Duration.ofDays(i));
    }

    void insert(Car car) {
        jdbcTemplate.update(
                """
                        INSERT INTO cars (id, owner_id, model, length_meters, weight_tons, registry_number)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """, ps -> {
                    ps.setObject(1, car.getId());
                    ps.setObject(2, car.getOwnerId());
                    ps.setString(3, car.getModel());
                    ps.setDouble(4, car.getLengthMeters());
                    ps.setDouble(5, car.getWeightTons());
                    ps.setString(6, car.getRegistryNumber());
                });
    }

    void insert(Employee employee) {
        jdbcTemplate.update(
                """
                        INSERT INTO employees (id, name, email, password)
                        VALUES (?, ?, ?, ?)
                        """, ps -> {
                    ps.setObject(1, employee.getId());
                    ps.setString(2, employee.getName());
                    ps.setString(3, employee.getEmail());
                    ps.setString(4, encoder.encode(employee.getPassword()));
                });
        for (Role role : employee.getRoles()) {
            jdbcTemplate.update(
                    """
                            INSERT INTO employee_roles (employee_id, role)
                            VALUES (?, ?)
                            """, ps -> {
                        ps.setObject(1, employee.getId());
                        ps.setString(2, role.name());
                    });
        }
    }

    void insert(Building building) {
        jdbcTemplate.update(
                """
                        INSERT INTO buildings (id, name, address, number_of_levels)
                        VALUES (?, ?, ?, ?)
                        """, ps -> {
                    ps.setObject(1, building.getId());
                    ps.setString(2, building.getName());
                    ps.setString(3, building.getAddress());
                    ps.setInt(4, building.getNumberOfLevels());
                });
    }

    void insert(ParkingLevel parkingLevel) {
        jdbcTemplate.update(
                """
                        INSERT INTO parking_levels (id, building_id, layer_name, number_of_spots, canvas)
                        VALUES (?, ?, ?, ?, ?::integer_pair)
                        """, ps -> {
                    ps.setObject(1, parkingLevel.getId());
                    ps.setObject(2, parkingLevel.getBuildingId());
                    ps.setString(3, parkingLevel.getLayerName());
                    ps.setInt(4, parkingLevel.getNumberOfSpots());
                    ps.setObject(5, String.format("(%d,%d)",
                            parkingLevel.getCanvas().getWidth(),
                            parkingLevel.getCanvas().getHeight()));
                });
    }

    void insert(ParkingSpot parkingSpot) {
        jdbcTemplate.update(
                """
                        INSERT INTO parking_spots (id, level_id, building_id, parking_number, is_available, is_free, canvas, on_canvas_coords)
                        VALUES (?, ?, ?, ?, ?, ?, ?::integer_pair, ?::integer_pair)
                        """, ps -> {
                    ps.setObject(1, parkingSpot.getId());
                    ps.setObject(2, parkingSpot.getLevelId());
                    ps.setObject(3, parkingSpot.getBuildingId());
                    ps.setString(4, parkingSpot.getParkingNumber());
                    ps.setObject(5, parkingSpot.getIsAvailable());
                    ps.setBoolean(6, parkingSpot.getIsFree());
                    ps.setObject(7, String.format("(%d,%d)",
                            parkingSpot.getCanvas().getWidth(),
                            parkingSpot.getCanvas().getHeight()));
                    ps.setObject(8, String.format("(%d,%d)",
                            parkingSpot.getOnCanvasCoords().getX(),
                            parkingSpot.getOnCanvasCoords().getY()));
                });
    }

    void deleteCars() {
        jdbcTemplate.update("DELETE FROM cars");
    }

    void deleteEmployees() {
        jdbcTemplate.update("DELETE FROM employees");
    }

    void deleteBuildings() {
        jdbcTemplate.update("DELETE FROM buildings");
    }

    void deleteParkingLevels() {
        jdbcTemplate.update("DELETE FROM parking_levels");
    }

    void deleteParkingSpots() {
        jdbcTemplate.update("DELETE FROM parking_spots");
    }

    void deleteReservations() {
        jdbcTemplate.update("DELETE FROM reservations");
    }

    @BeforeEach
    void setUpEach(WebApplicationContext webApplicationContext) throws Exception {
        insert(employeeAlice);
        insert(employeeBob);

        MvcResult resultPost = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(employeeAlice)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        adminToken = "Bearer " + JsonPath.read(resultPost.getResponse().getContentAsString(), "$.accessToken");

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilter(new CharacterEncodingFilter("UTF-8", false))
                .addFilter(new JwtHeaderFilter(adminToken))
                .apply(springSecurity()).build();

        insert(carSupraOfAlice);
        insert(carSkylineOfAlice);
        insert(carAudiOfBob);
        insert(carTeslaOfBob);

        insert(building);
        insert(parkingLevelOne);
        insert(parkingLevelTwo);
        insert(parkingSpotA);
        insert(parkingSpotB);
        insert(parkingSpotC);
        insert(parkingSpotD);
        insert(parkingSpotE);
    }

    @AfterEach
    void tearDownEach() {
        deleteEmployees();
        deleteCars();
        deleteBuildings();
        deleteParkingLevels();
        deleteParkingSpots();
        deleteReservations();
        carCache.clear();
        employeeCache.clear();
        buildingCache.clear();
        parkingLevelCache.clear();
        parkingSpotCache.clear();
        reservationCache.clear();

        scheduledTasksCache.forEach((uuid, pair) -> {
            pair.first().cancel(true);
            pair.second().cancel(true);
        });
        scheduledTasksCache.clear();
    }
}
