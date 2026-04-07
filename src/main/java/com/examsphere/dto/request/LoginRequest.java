package com.examsphere.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class LoginRequest {
    
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password must not be blank")
    String password;
}
