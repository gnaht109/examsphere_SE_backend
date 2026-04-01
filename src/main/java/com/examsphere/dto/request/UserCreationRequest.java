package com.examsphere.dto.request;

import com.examsphere.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UserCreationRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 6, max = 100, message = "Full name must be between 2 and 100 characters")
    String fullName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    String password;

    @NotNull(message = "Role must not be null")
    UserRole role;
}
