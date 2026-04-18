package com.examsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.examsphere.config.JwtAuthenticationFilter;
import com.examsphere.config.JwtUtils;
import com.examsphere.config.SecurityConfig;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.enums.UserRole;
import com.examsphere.service.UserService;

@WebMvcTest({UserController.class, AdminUserController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserSecurityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtUtils jwtUtils;

    @Test
    void signup_isPublicAndCreatesStudent() throws Exception {
        when(userService.createStudent(any())).thenReturn(UserResponse.builder()
                .id(1L)
                .username("student")
                .role(UserRole.STUDENT)
                .build());

        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student",
                                  "fullName": "Student User",
                                  "email": "student@test.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("STUDENT"));

        verify(userService).createStudent(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createTeacher_forbiddenForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/users/teachers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "teacher",
                                  "fullName": "Teacher User",
                                  "email": "teacher@test.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTeacher_allowedForAdmin() throws Exception {
        when(userService.createTeacher(any())).thenReturn(UserResponse.builder()
                .id(2L)
                .username("teacher")
                .role(UserRole.TEACHER)
                .build());

        mockMvc.perform(post("/api/admin/users/teachers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "teacher",
                                  "fullName": "Teacher User",
                                  "email": "teacher@test.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("TEACHER"));

        verify(userService).createTeacher(any());
    }
}
