package com.examsphere.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.examsphere.enums.UserRole;
import com.examsphere.model.User;
import com.examsphere.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    
    PasswordEncoder passwordEncoder;


    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
            User admin = User.builder()
                .username("admin")
                .email("admin@examsphere.com")
                .password(passwordEncoder.encode("admin"))
                .role(UserRole.ADMIN)
                .build();
            userRepository.save(admin);
            }
        };
    }
}
