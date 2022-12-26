package org.hse.parkings.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.hse.parkings.model.building.CanvasSize;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CanvasSizeTypeHandler extends BaseTypeHandler<CanvasSize> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i,
                                    CanvasSize canvasSize, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,
                String.format("(%d,%d)", canvasSize.getWidth(), canvasSize.getHeight()));
    }

    @Override
    public CanvasSize getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String[] parsed = resultSet.getString(s).replaceAll("^\\D+", "").split("\\D+");
        return new CanvasSize(Integer.parseInt(parsed[0]), Integer.parseInt(parsed[1]));
    }

    @Override
    public CanvasSize getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getObject(i, CanvasSize.class);
    }

    @Override
    public CanvasSize getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getObject(i, CanvasSize.class);
    }
}
