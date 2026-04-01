package com.examsphere.mapper;

import org.mapstruct.Mapper;

import com.examsphere.dto.request.UserCreationRequest;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
}