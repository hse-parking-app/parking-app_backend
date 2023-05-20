package org.hse.parkings.security;

import io.jsonwebtoken.Claims;
import org.hse.parkings.model.employee.Role;
import org.hse.parkings.model.jwt.JwtAuthentication;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        return JwtAuthentication.builder()
                .roles(getRoles(claims))
                .id(UUID.fromString(claims.get("employee_id", String.class)))
                .name(claims.get("name", String.class))
                .email(claims.getSubject()).build();
    }

    @SuppressWarnings("unchecked")
    private static Set<Role> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);

        return roles.stream().map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
