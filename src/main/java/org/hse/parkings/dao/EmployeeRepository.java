package org.hse.parkings.dao;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.model.Employee;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface EmployeeRepository {

    @Select("SELECT id, name, email, password FROM employees WHERE id = #{id}::uuid")
    @Results(id = "employeeResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name"),
            @Result(column = "email", property = "email"),
            @Result(column = "password", property = "password")
    })
    Optional<Employee> findById(UUID id);

    @Select("SELECT id, name, email, password FROM employees WHERE email = #{email}")
    @ResultMap("employeeResultMap")
    Optional<Employee> findByEmail(String email);

    @Select("SELECT * FROM employees")
    @ResultMap("employeeResultMap")
    Set<Employee> findAll();

    @Insert("""
            INSERT INTO employees (id, name, email, password)
            VALUES (#{id}::uuid, #{name}, #{email}, #{password})
            """
    )
    void save(Employee employee);

    @Update("""
            UPDATE employees SET name = #{name}, email = #{email}, password = #{password}
            WHERE id = #{id}::uuid
            """
    )
    void update(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM employees")
    void deleteAll();
}
