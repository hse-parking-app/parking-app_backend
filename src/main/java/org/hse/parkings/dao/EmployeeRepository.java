package org.hse.parkings.dao;

import org.apache.ibatis.annotations.*;
import org.hse.parkings.model.employee.Employee;
import org.hse.parkings.model.employee.Role;

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
            @Result(column = "id", javaType = Set.class, property = "roles",
                    many = @Many(select = "findRolesById"))
    })
    Optional<Employee> findById(UUID id);

    @Select("SELECT id, name, email, password FROM employees WHERE email = #{email}")
    @ResultMap("employeeResultMap")
    Optional<Employee> findByEmail(String email);

    @Select("SELECT * FROM employees")
    @ResultMap("employeeResultMap")
    Set<Employee> findAll();

    @Select("SELECT role FROM employee_roles WHERE employee_id = #{id}::uuid")
    Set<Role> findRolesById(UUID id);

    @Insert("""
            <script>
            INSERT INTO employees (id, name, email, password)
            VALUES (#{id}::uuid, #{name}, #{email}, #{password});
            INSERT INTO employee_roles (employee_id, role)
            VALUES
            <foreach item="item" collection="roles" separator=",">
                (#{id}::uuid, #{item})
            </foreach>
            </script>
            """)
    void save(Employee employee);

    @Update("""
            <script>
            UPDATE employees SET name = #{name}, email = #{email}, password = #{password}
            WHERE id = #{id}::uuid;
            DELETE FROM employee_roles WHERE employee_id = #{id}::uuid;
            <foreach item="item" collection="roles" separator=";">
                INSERT INTO employee_roles (employee_id, role)
                SELECT #{id}::uuid, #{item}
                WHERE EXISTS (SELECT * FROM employees WHERE id = #{id}::uuid)
            </foreach>
            </script>
            """)
    void update(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}::uuid")
    void delete(UUID id);

    @Delete("DELETE FROM employees")
    void deleteAll();

    @Select("SELECT refresh_token FROM employee_tokens WHERE email = #{email}")
    String getRefreshToken(String email);

    @Insert("""
            INSERT INTO employee_tokens (email, refresh_token)
            VALUES (#{email}, #{refreshToken})
            ON CONFLICT (email) DO UPDATE
                    SET refresh_token = #{refreshToken}
            """)
    void putRefreshToken(String email, String refreshToken);

    @Delete("DELETE FROM employee_tokens")
    void deleteAllRefreshTokens();
}
