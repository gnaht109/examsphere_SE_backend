package com.examsphere.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examsphere.dto.request.TeacherCreationRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;

    @PostMapping("/teachers")
    ApiResponse<UserResponse> createTeacher(@Valid @RequestBody TeacherCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createTeacher(request))
                .build();
    }
}
