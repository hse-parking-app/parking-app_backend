package org.hse.parkings.dao;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.model.Car;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface CarRepository {

    @Select("SELECT id, owner_id, model, length_meters, weight_tons, registry_number FROM cars WHERE id = #{id}::uuid")
    @Results(id = "carResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "owner_id", property = "ownerId"),
            @Result(column = "model", property = "model"),
            @Result(column = "length_meters", property = "lengthMeters"),
            @Result(column = "weight_tons", property = "weightTons"),
            @Result(column = "registry_number", property = "registryNumber"),
    })
    Optional<Car> find(UUID id);

    @Select("SELECT * FROM cars WHERE owner_id = #{employeeId}::uuid")
    Set<Car> findEmployeesCars(UUID employeeId);

    @Select("SELECT * FROM cars")
    @ResultMap("carResultMap")
    Set<Car> findAll();

    @Insert("""
            INSERT INTO cars (id, owner_id, model, length_meters, weight_tons, registry_number)
            VALUES (#{id}::uuid, #{ownerId}::uuid, #{model}, #{lengthMeters}, #{weightTons}, #{registryNumber})
            """)
    void save(Car car);

    @Update("""
            UPDATE cars
            SET model = #{model}, length_meters = #{lengthMeters},
            weight_tons = #{weightTons}, registry_number = #{registryNumber}
            WHERE id = #{id}::uuid
            """)
    void update(Car car);

    @Delete("DELETE FROM cars WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM cars")
    void deleteAll();
}
