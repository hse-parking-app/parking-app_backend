package org.hse.parkings.dao.building;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.handler.type.CanvasSizeTypeHandler;
import org.hse.parkings.handler.type.OnCanvasCoordsTypeHandler;
import org.hse.parkings.model.building.ParkingSpot;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface ParkingSpotRepository {

    @Select("""
            SELECT id, level_id, building_id, parking_number, is_available, is_free, canvas, on_canvas_coords FROM parking_spots
            WHERE id = #{id}::uuid
            """
    )
    @Results(id = "parkingSpotResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "level_id", property = "levelId"),
            @Result(column = "building_id", property = "buildingId"),
            @Result(column = "parking_number", property = "parkingNumber"),
            @Result(column = "is_available", property = "isAvailable"),
            @Result(column = "is_free", property = "isFree"),
            @Result(column = "canvas", property = "canvas", typeHandler = CanvasSizeTypeHandler.class),
            @Result(column = "on_canvas_coords", property = "onCanvasCoords", typeHandler = OnCanvasCoordsTypeHandler.class)
    })
    Optional<ParkingSpot> find(UUID id);

    @Select("SELECT * FROM parking_spots")
    @ResultMap("parkingSpotResultMap")
    Set<ParkingSpot> findAll();

    @Insert("""
            INSERT INTO parking_spots (id, level_id, building_id, parking_number, is_available, is_free, canvas, on_canvas_coords)
            VALUES (#{id}::uuid, #{levelId}::uuid, #{buildingId}::uuid, #{parkingNumber}, #{isAvailable}, #{isFree},
            #{canvas}::integer_pair, #{onCanvasCoords}::integer_pair)
            """)
    void save(ParkingSpot parkingSpot);

    @Update("""
            UPDATE parking_spots SET
            level_id = #{levelId}, building_id = #{buildingId}, parking_number = #{parkingNumber},
            is_available = #{isAvailable}, is_free = #{isFree}, canvas = #{canvas}::integer_pair,
            on_canvas_coords = #{onCanvasCoords}::integer_pair
            WHERE id = #{id}::uuid
            """)
    void update(ParkingSpot parkingSpot);

    @Delete("DELETE FROM parking_spots WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM parking_spots")
    void deleteAll();

    @Update("UPDATE parking_spots SET is_free = true")
    void freeAllParkingSpots();

    @Update("UPDATE parking_spots SET is_free = false WHERE id = #{id}::uuid")
    void occupySpot(UUID id);

    @Update("UPDATE parking_spots SET is_free = true WHERE id = #{id}::uuid")
    void freeSpot(UUID id);
}
