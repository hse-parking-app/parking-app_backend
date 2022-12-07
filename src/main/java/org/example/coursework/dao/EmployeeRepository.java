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
import org.example.coursework.model.Employee;

@Mapper
public interface EmployeeRepository {
    @Select("SELECT id, name FROM employees WHERE id = #{id}::uuid")
    @Results(id = "employeeResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
    })
    Optional<Employee> find(UUID id);

    @Select("SELECT id, name FROM employees")
    @ResultMap("employeeResultMap")
    Set<Employee> findAll();

    @Insert("INSERT INTO employees (id, name) " +
            "VALUES (#{id}::uuid, #{name})")
    void save(Employee employee);

    @Update("UPDATE employees SET name = #{name} " +
            "WHERE id = #{id}::uuid")
    void update(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM employees")
    void deleteAll();
}
