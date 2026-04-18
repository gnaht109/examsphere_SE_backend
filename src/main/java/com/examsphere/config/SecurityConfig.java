package com.examsphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Hiện tại cho phép tất cả để nhóm dễ làm code
                // .requestMatchers("/api/auth/**", "/api/users/**").permitAll() 
                // .anyRequest().permitAll() 
                
                //KHI NÀO XONG THÌ ĐỔI LẠI THÀNH:
                .requestMatchers("/api/auth/login", "/api/users/signup").permitAll()

                //teacher endpoints
                .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                
                .anyRequest().authenticated() 
                
            )
            .addFilterBefore(jwtFilter, 
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            ;
                
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
