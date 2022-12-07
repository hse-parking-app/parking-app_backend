package org.example.coursework.dao;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.coursework.model.Reservation;

@Mapper
public interface ReservationRepository {
    @Select("SELECT id, car_id, employee_id, parking_spot_id, start_time, end_time FROM reservations " +
            "WHERE id = #{id}::uuid")
    @Results(id = "reservationResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "car_id", property = "carId"),
            @Result(column = "employee_id", property = "employeeId"),
            @Result(column = "parking_spot_id", property = "parkingSpotId"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
    })
    Optional<Reservation> find(UUID id);

    @Select("SELECT id, car_id, employee_id, parking_spot_id, start_time, end_time FROM reservations")
    @ResultMap("reservationResultMap")
    Set<Reservation> findAll();

    @Insert("INSERT INTO reservations (id, car_id, employee_id, parking_spot_id, start_time, end_time) " +
            "VALUES (#{id}::uuid, #{carId}::uuid, #{employeeId}::uuid, #{parkingSpotId}::uuid, #{startTime}, #{endTime})")
    void save(Reservation reservation);

    @Update("UPDATE reservations " +
            "SET car_id = #{carId}::uuid, employee_id = #{employeeId}::uuid, parking_spot_id = #{parkingSpotId}::uuid, " +
            "start_time = #{startTime}, end_time = #{endTime} " +
            "WHERE id = #{id}::uuid")
    void update(Reservation reservation);

    @Delete("DELETE FROM reservations WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM reservations")
    void deleteAll();

    @Select("""
            SELECT id, car_id, employee_id, parking_spot_id, start_time, end_time FROM reservations 
            WHERE parking_spot_id = #{parkingSpotId}::uuid AND 
                ((#{startTime} < start_time AND #{endTime} > start_time) OR (#{startTime} > start_time AND #{startTime} < end_time))
                AND id <> #{id}::uuid 
            LIMIT 1
            """)
    @ResultMap("reservationResultMap")
    Set<Reservation> getParkingSpotTimeCollisions(Reservation reservation);

    @Select("""
            SELECT id, car_id, employee_id, parking_spot_id, start_time, end_time FROM reservations 
            WHERE car_id = #{carId}::uuid AND 
                ((#{startTime} < start_time AND #{endTime} > start_time) OR (#{startTime} > start_time AND #{startTime} < end_time))
                AND id <> #{id}::uuid 
            LIMIT 1
            """)
    @ResultMap("reservationResultMap")
    Set<Reservation> getCarTimeCollisions(Reservation reservation);

}
