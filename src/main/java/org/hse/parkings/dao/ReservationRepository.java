package org.hse.parkings.dao;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.model.Reservation;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface ReservationRepository {

    @Select("""
            SELECT id, car_id, employee_id, spot_id, start_time, end_time FROM reservations
            WHERE id = #{id}::uuid
            """)
    @Results(id = "reservationResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "car_id", property = "carId"),
            @Result(column = "employee_id", property = "employeeId"),
            @Result(column = "spot_id", property = "parkingSpotId"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
    })
    Optional<Reservation> find(UUID id);

    @Select("SELECT * FROM reservations WHERE employee_id = #{employeeId}::uuid")
    Set<Reservation> findEmployeeReservations(UUID employeeId);

    @Select("SELECT * FROM reservations")
    @ResultMap("reservationResultMap")
    Set<Reservation> findAll();

    @Insert("""
            INSERT INTO reservations (id, car_id, employee_id, spot_id, start_time, end_time)
            VALUES (#{id}::uuid, #{carId}::uuid, #{employeeId}::uuid, #{parkingSpotId}::uuid, #{startTime}, #{endTime})
            """)
    void save(Reservation reservation);

    @Update("""
            UPDATE reservations
            SET car_id = #{carId}::uuid, employee_id = #{employeeId}::uuid, spot_id = #{parkingSpotId}::uuid,
            start_time = #{startTime}, end_time = #{endTime}
            WHERE id = #{id}::uuid
            """)
    void update(Reservation reservation);

    @Delete("DELETE FROM reservations WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM reservations")
    void deleteAll();

    @Delete("DELETE FROM reservations WHERE end_time < #{localDateTime}")
    void deleteExpiredReservations(LocalDateTime localDateTime);

    @Select("""
            SELECT id, car_id, employee_id, spot_id, start_time, end_time FROM reservations
            WHERE spot_id = #{parkingSpotId}::uuid AND
                ((#{startTime} <= start_time AND #{endTime} > start_time) OR (#{startTime} >= start_time AND #{startTime} < end_time))
                AND id <> #{id}::uuid
            LIMIT 1
            """)
    @ResultMap("reservationResultMap")
    Set<Reservation> getParkingSpotTimeCollisions(Reservation reservation);

    @Select("""
            SELECT id, car_id, employee_id, spot_id, start_time, end_time FROM reservations
            WHERE car_id = #{carId}::uuid AND
                ((#{startTime} <= start_time AND #{endTime} > start_time) OR (#{startTime} >= start_time AND #{startTime} < end_time))
                AND id <> #{id}::uuid
            LIMIT 1
            """)
    @ResultMap("reservationResultMap")
    Set<Reservation> getCarTimeCollisions(Reservation reservation);

    @Select("""
            SELECT r.id, r.car_id, r.employee_id, r.spot_id, r.start_time, r.end_time FROM reservations r
            INNER JOIN parking_spots ps ON r.spot_id = ps.id
                WHERE ps.level_id = #{levelId}::uuid AND
                ((#{startTime} <= start_time AND #{endTime} > start_time) OR (#{startTime} >= start_time AND #{startTime} < end_time))
            """)
    @ResultMap("reservationResultMap")
    Set<Reservation> getReservationsOnParkingLevelInInterval(UUID levelId, LocalDateTime startTime, LocalDateTime endTime);
}
