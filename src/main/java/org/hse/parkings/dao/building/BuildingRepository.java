package org.hse.parkings.dao.building;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.model.building.Building;
import org.hse.parkings.model.building.ParkingLevel;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface BuildingRepository {

    @Select("SELECT id, name, address, number_of_levels FROM buildings WHERE id = #{id}::uuid")
    @Results(id = "buildingResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "address", property = "address"),
            @Result(column = "number_of_levels", property = "numberOfLevels"),
    })
    Optional<Building> find(UUID id);

    @Select("SELECT * FROM buildings")
    @ResultMap("buildingResultMap")
    Set<Building> findAll();

    @Insert("""
            INSERT INTO buildings(id, name, address, number_of_levels)
            VALUES (#{id}::uuid, #{name}, #{address}, #{numberOfLevels})
            """
    )
    void save(Building building);

    @Update("""
            UPDATE buildings
            SET name = #{name}, address = #{address}, number_of_levels = #{numberOfLevels}
            WHERE id = #{id}::uuid
            """
    )
    void update(Building building);

    @Delete("DELETE FROM buildings WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM buildings")
    void deleteAll();

    @Select("SELECT * FROM parking_levels WHERE building_id = #{buildingId}::uuid ORDER BY layer_name")
    List<ParkingLevel> findBuildingLevels(UUID buildingId);
}
