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
import org.example.coursework.model.ParkingSpot;

@Mapper
public interface ParkingSpotRepository {
    @Select("SELECT id, parking_number, is_free FROM parking_spots WHERE id = #{id}::uuid")
    @Results(id = "parkingSpotResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "parking_number", property = "parkingNumber"),
            @Result(column = "is_free", property = "isFree"),
    })
    Optional<ParkingSpot> find(UUID id);

    @Select("SELECT id, parking_number, is_free FROM parking_spots")
    @ResultMap("parkingSpotResultMap")
    Set<ParkingSpot> findAll();

    @Insert("INSERT INTO parking_spots (id, parking_number, is_free) " +
            "VALUES (#{id}::uuid, #{parkingNumber}, #{isFree})")
    void save(ParkingSpot parkingSpot);

    @Update("UPDATE parking_spots SET parking_number = #{parkingNumber}, is_free = #{isFree} " +
            "WHERE id = #{id}::uuid")
    void update(ParkingSpot parkingSpot);

    @Delete("DELETE FROM parking_spots WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM parking_spots")
    void deleteAll();
}
