package org.example.coursework.dao;

import org.apache.ibatis.annotations.*;
import org.example.coursework.model.Car;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface CarRepository {

    @Select("SELECT id, model, dimension_length, dimension_wight, registry_number FROM cars WHERE id = #{id}::uuid")
    @Results(id = "carResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "model", property = "model"),
            @Result(column = "dimension_length", property = "length"),
            @Result(column = "dimension_wight", property = "wight"),
            @Result(column = "registry_number", property = "registryNumber"),
    })
    Optional<Car> find(UUID id);

    @Select("SELECT * FROM cars")
    @ResultMap("carResultMap")
    Set<Car> findAll();

    @Insert("INSERT INTO cars (id, model, dimension_length, dimension_wight, registry_number) " +
            "VALUES (#{id}::uuid, #{model}, #{length}, #{wight}, #{registryNumber})")
    void save(Car car);

    @Update("UPDATE cars SET model = #{model}, dimension_length = #{length}, " +
            "dimension_wight = #{wight}, registry_number = #{registryNumber} " +
            "WHERE id = #{id}::uuid")
    void update(Car car);

    @Delete("DELETE FROM cars WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM cars")
    void deleteAll();
}
