package org.hse.parkings.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hse.parkings.model.error.CauseMessage;
import org.hse.parkings.model.error.Error;
import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final String[] endpointsExcludedFromJwt = new String[]{
            "/auth/signUp",
            "/auth/login",
            "/auth/update/access",
            "/time/current",
            "/actuator/**"
    };

    private final JwtFilter jwtFilter;

    private final ObjectMapper jackson;

    private final DateTimeProvider dateTimeProvider;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    void configureInspector(
            AuthenticationManagerBuilder auth,
            PasswordEncoder encoder,
            @Value("${auth.inspector.login}") String user,
            @Value("${auth.inspector.password}") String password
    ) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(user)
                .password(encoder.encode(password))
                .roles("INSPECTOR");
    }

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorWebSecurity(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
                .and()
                .requestMatchers(matchers -> matchers
                        .antMatchers("/actuator/**"))
                .authorizeHttpRequests(request -> request
                        .anyRequest().authenticated()).build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultWebSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((this::handleAuthenticationException))
                .and()
                .authorizeHttpRequests(
                        request -> request
                                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger/**", "/swagger*").permitAll()
                                .antMatchers(endpointsExcludedFromJwt).permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                ).build();
    }

    private void handleAuthenticationException(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {
        res.setContentType("application/json");
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.getWriter().write(jackson.writeValueAsString(new Error(
                HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.value(),
                dateTimeProvider.getZonedDateTime(),
                Collections.singletonList(new CauseMessage("error", ex.getMessage())),
                "uri=" + req.getRequestURI()
        )));
    }
}
