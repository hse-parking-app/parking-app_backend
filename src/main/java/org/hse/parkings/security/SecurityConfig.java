package org.hse.parkings.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final String[] excludedFromJwtEndpoints = new String[]{"/auth/signUp", "/auth/login",
            "/auth/update/access", "/auth/update/refresh"};

    private final JwtFilter jwtFilter;

    private final ObjectMapper jackson;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(((request, response, ex) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write(jackson.writeValueAsString(new Error(
                            HttpStatus.FORBIDDEN,
                            HttpStatus.FORBIDDEN.value(),
                            DateTimeProvider.getInstance().getZonedDateTime(),
                            Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                            "uri=" + request.getServletPath()
                    )));
                }))
                .and()
                .authorizeHttpRequests(
                        request -> request
                                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger/**", "/swagger*").permitAll()
                                .antMatchers(excludedFromJwtEndpoints).permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                ).build();
    }
}
