package com.examsphere.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.examsphere.dto.request.StudentSignupRequest;
import com.examsphere.dto.request.TeacherCreationRequest;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.enums.UserRole;
import com.examsphere.mapper.UserMapper;
import com.examsphere.model.User;
import com.examsphere.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void createStudent_alwaysAssignsStudentRole() {
        StudentSignupRequest request = StudentSignupRequest.builder()
                .username("student")
                .fullName("Student User")
                .email("student@test.com")
                .password("123456")
                .build();
        User mappedUser = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .build();
        User savedUser = User.builder()
                .id(1L)
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(UserRole.STUDENT)
                .build();
        UserResponse response = UserResponse.builder()
                .id(1L)
                .username(request.getUsername())
                .role(UserRole.STUDENT)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.createStudent(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(result.getRole()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void createTeacher_alwaysAssignsTeacherRole() {
        TeacherCreationRequest request = TeacherCreationRequest.builder()
                .username("teacher")
                .fullName("Teacher User")
                .email("teacher@test.com")
                .password("123456")
                .build();
        User mappedUser = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .build();
        User savedUser = User.builder()
                .id(2L)
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(UserRole.TEACHER)
                .build();
        UserResponse response = UserResponse.builder()
                .id(2L)
                .username(request.getUsername())
                .role(UserRole.TEACHER)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.createTeacher(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.TEACHER);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(result.getRole()).isEqualTo(UserRole.TEACHER);
    }
}
