package com.examsphere.model;

import java.time.LocalDateTime;
import com.examsphere.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;
    String fullName;
    
    @jakarta.persistence.Column(unique = true, nullable = false)
    String email;

    @jakarta.persistence.Column(nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    UserRole role;
    LocalDateTime createdAt;
    @PrePersist
    void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
