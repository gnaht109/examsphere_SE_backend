package com.examsphere.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.examsphere.dto.request.UserCreationRequest;
import com.examsphere.dto.response.UserResponse;
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

    //Sign up login
    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
