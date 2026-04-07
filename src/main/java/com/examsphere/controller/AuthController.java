package com.examsphere.controller;

import org.springframework.web.bind.annotation.*;
import com.examsphere.dto.request.LoginRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.JwtResponse;
import com.examsphere.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/auth") 
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<JwtResponse>builder()
                .data(authService.login(request))
                .build();
    }
}
