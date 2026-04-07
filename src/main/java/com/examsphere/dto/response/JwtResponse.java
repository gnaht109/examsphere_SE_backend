package com.examsphere.dto.response;

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
public class JwtResponse {
    String token;
    String type = "Bearer";
    String email;
    UserRole role;
}
