package org.hse.parkings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hse.parkings.model.Car;
import org.hse.parkings.model.building.*;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtResponse;
import org.hse.parkings.utils.DateTimeProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Collections;

import static org.hse.parkings.service.ReservationService.reservationsQueue;
import static org.hse.parkings.utils.Cache.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random.class)
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
    protected final Employee employeeDarlene = Employee.builder()
            .name("Darlene")
            .email("d@d.d")
            .password("123")
            .roles(Collections.singleton(Role.APP_USER)).build();

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
    protected final Car carPorscheOfDarlene = Car.builder()
            .ownerId(employeeDarlene.getId())
            .model("Porsche 911")
            .lengthMeters(1.2)
            .weightTons(1.0)
            .registryNumber("ABC-125").build();

    protected final Building building = Building.builder()
            .name("Building")
            .address("Street")
            .numberOfLevels(1).build();
    protected final ParkingLevel parkingLevelOne = ParkingLevel.builder()
            .buildingId(building.getId())
            .levelNumber(0)
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
            .levelNumber(1)
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

    protected String bearer = "Bearer ";
    protected JwtResponse tokens = null;

    protected String buildingEndpoint = "/building";
    protected String parkingLevelsEndpoint = "/parkingLevels";
    protected String parkingSpotsEndpoint = "/parkingSpots";
    protected String authEndpoint = "/auth";
    protected String carsEndpoint = "/cars";
    protected String employeesEndpoint = "/employees";
    protected String reservationsEndpoint = "/reservations";
    protected String timeEndpoint = "/time";

    @Autowired
    protected DateTimeProvider dateTimeProvider;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected PasswordEncoder encoder;

    @Autowired
    protected ObjectMapper jackson;

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

    protected void loginAs(Employee employee) throws Exception {
        MvcResult resultPost = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        tokens = jackson.readValue(resultPost.getResponse().getContentAsString(), JwtResponse.class);
    }

    @BeforeAll
    void setUpEmployees() {
        insert(employeeAlice);
        insert(employeeBob);
        insert(employeeDarlene);
    }

    @BeforeEach
    void setUpEach(WebApplicationContext webApplicationContext) {
        insert(carSupraOfAlice);
        insert(carSkylineOfAlice);
        insert(carAudiOfBob);
        insert(carTeslaOfBob);
        insert(carPorscheOfDarlene);

        insert(building);
        insert(parkingLevelOne);
        insert(parkingLevelTwo);
        insert(parkingSpotA);
        insert(parkingSpotB);
        insert(parkingSpotC);
        insert(parkingSpotD);
        insert(parkingSpotE);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilter(new CharacterEncodingFilter("UTF-8", false))
                .apply(springSecurity()).build();
    }

    @AfterAll
    void tearDownEmployees() {
        deleteEmployees();
    }

    @AfterEach
    void tearDownEach() {
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
        reservationsQueue.clear();
    }

    protected void insert(Car car) {
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

    protected void insert(Employee employee) {
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

    protected void insert(Building building) {
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

    protected void insert(ParkingLevel parkingLevel) {
        jdbcTemplate.update(
                """
                        INSERT INTO parking_levels (id, building_id, level_number, number_of_spots, canvas)
                        VALUES (?, ?, ?, ?, ?::integer_pair)
                        """, ps -> {
                    ps.setObject(1, parkingLevel.getId());
                    ps.setObject(2, parkingLevel.getBuildingId());
                    ps.setInt(3, parkingLevel.getLevelNumber());
                    ps.setInt(4, parkingLevel.getNumberOfSpots());
                    ps.setObject(5, String.format("(%d,%d)",
                            parkingLevel.getCanvas().getWidth(),
                            parkingLevel.getCanvas().getHeight()));
                });
    }

    protected void insert(ParkingSpot parkingSpot) {
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

    protected void deleteCars() {
        jdbcTemplate.update("DELETE FROM cars");
    }

    protected void deleteEmployees() {
        jdbcTemplate.update("DELETE FROM employees");
    }

    protected void deleteBuildings() {
        jdbcTemplate.update("DELETE FROM buildings");
    }

    protected void deleteParkingLevels() {
        jdbcTemplate.update("DELETE FROM parking_levels");
    }

    protected void deleteParkingSpots() {
        jdbcTemplate.update("DELETE FROM parking_spots");
    }

    protected void deleteReservations() {
        jdbcTemplate.update("DELETE FROM reservations");
    }
}
