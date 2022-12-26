package org.hse.parkings.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.hse.parkings.model.building.OnCanvasCoords;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OnCanvasCoordsTypeHandler extends BaseTypeHandler<OnCanvasCoords> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i,
                                    OnCanvasCoords canvasSize, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                String.format("(%d,%d)", canvasSize.getX(), canvasSize.getY()));
    }

    @Override
    public OnCanvasCoords getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String[] parsed = resultSet.getString(s).replaceAll("^\\D+", "").split("\\D+");
        return new OnCanvasCoords(Integer.parseInt(parsed[0]), Integer.parseInt(parsed[1]));
    }

    @Override
    public OnCanvasCoords getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getObject(i, OnCanvasCoords.class);
    }

    @Override
    public OnCanvasCoords getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i, OnCanvasCoords.class);
    }
}
