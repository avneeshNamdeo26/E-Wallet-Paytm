package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.UserConstants.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class UserSecurityConfig{

    @Autowired
    UserService userService;

    @Autowired
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder.userDetailsService(userService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.httpBasic(withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authenticationManagerBuilder ->
                        authenticationManagerBuilder
                                .requestMatchers(HttpMethod.POST,"/user/**").permitAll()
                                .requestMatchers("/user/**").hasAuthority(USER_AUTHORITY)
                                .requestMatchers("/admin/**").hasAnyAuthority(ADMIN_AUTHORITY,SERVICE_AUTHORITY)
                )
                .formLogin(withDefaults());
        return http.build();
    }
}
