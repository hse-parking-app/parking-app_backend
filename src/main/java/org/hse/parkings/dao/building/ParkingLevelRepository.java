package org.hse.parkings.dao.building;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.handler.type.CanvasSizeTypeHandler;
import org.hse.parkings.model.building.ParkingLevel;
import org.hse.parkings.model.building.ParkingSpot;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface ParkingLevelRepository {

    @Select("SELECT id, building_id, layer_name, number_of_spots, canvas FROM parking_levels WHERE id = #{id}::uuid")
    @Results(id = "levelsResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "building_id", property = "buildingId"),
            @Result(column = "layer_name", property = "layerName"),
            @Result(column = "number_of_spots", property = "numberOfSpots"),
            @Result(column = "canvas", property = "canvas", typeHandler = CanvasSizeTypeHandler.class)
    })
    Optional<ParkingLevel> find(UUID id);

    @Select("SELECT * FROM parking_levels")
    @ResultMap("levelsResultMap")
    Set<ParkingLevel> findAll();

    @Insert("""
            INSERT INTO parking_levels (id, building_id, layer_name, number_of_spots, canvas)
            VALUES (#{id}::uuid, #{buildingId}::uuid, #{layerName}, #{numberOfSpots}, #{canvas}::integer_pair)
            """)
    void save(ParkingLevel parkingLevel);

    @Update("""
            UPDATE parking_levels
            SET building_id = #{buildingId}, layer_name = #{layerName}, number_of_spots = #{numberOfSpots},
            canvas = #{canvas}::integer_pair
            WHERE id = #{id}::uuid
            """)
    void update(ParkingLevel parkingLevel);

    @Delete("DELETE FROM parking_levels WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM parking_levels")
    void deleteAll();

    @Select("SELECT * FROM parking_spots WHERE level_id = #{levelId}::uuid")
    Set<ParkingSpot> findParkingSpots(UUID levelId);
}
