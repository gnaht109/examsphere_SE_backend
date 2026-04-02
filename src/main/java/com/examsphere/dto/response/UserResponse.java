package com.examsphere.dto.response;

import java.time.LocalDateTime;

import com.examsphere.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserResponse {

    Long id;
    String username;
    String fullName;
    String email;
    UserRole role;
    LocalDateTime createdAt;

}
