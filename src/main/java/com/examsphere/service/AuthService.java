package com.examsphere.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.examsphere.config.JwtUtils;
import com.examsphere.dto.request.LoginRequest;
import com.examsphere.dto.response.JwtResponse;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.model.User;
import com.examsphere.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;

    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()); 
        
        if (user == null){
            throw new AppException(ErrorCode.USER_NOT_FOUND); 
        }
        
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
            String token = jwtUtils.generateToken(user); 
        
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String username = (String) authentication.getPrincipal();

        return userRepository.findByUsername(username)
                //.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();

        return userRepository.findByUsername(username);
                //.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

}
