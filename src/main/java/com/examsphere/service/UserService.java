package com.examsphere.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.examsphere.dto.request.StudentSignupRequest;
import com.examsphere.dto.request.TeacherCreationRequest;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.enums.UserRole;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.mapper.UserMapper;
import com.examsphere.model.User;
import com.examsphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;



@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE,makeFinal=true)
public class UserService {
    //AuthContextService authContextService;
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createStudent(StudentSignupRequest request) {
        User user = userMapper.toUser(request);
        return saveUserWithRole(user, request.getPassword(), UserRole.STUDENT);
    }

    public UserResponse createTeacher(TeacherCreationRequest request) {
        User user = userMapper.toUser(request);
        return saveUserWithRole(user, request.getPassword(), UserRole.TEACHER);
    }

    UserResponse saveUserWithRole(User user, String rawPassword, UserRole role) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(rawPassword));

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
