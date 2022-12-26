package org.hse.parkings.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger/**", "/swagger*").permitAll()
                .and().authorizeRequests().anyRequest().authenticated()
                .and().csrf().disable();
    }

    @Autowired
    protected void configureAdmin(
            AuthenticationManagerBuilder auth,
            PasswordEncoder encoder,
            @Value("${auth.admin.login}") String adminLogin,
            @Value("${auth.admin.password}") String adminPass
    ) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(adminLogin)
                .password(encoder.encode(adminPass))
                .roles("ADMIN");
    }

    @Autowired
    protected void configureUser(
            AuthenticationManagerBuilder auth,
            PasswordEncoder encoder,
            @Value("${auth.user.login}") String userLogin,
            @Value("${auth.user.password}") String userPass
    ) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(userLogin)
                .password(encoder.encode(userPass))
                .roles("USER");
    }
}
