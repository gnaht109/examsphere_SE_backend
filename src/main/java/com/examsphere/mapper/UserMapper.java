package com.examsphere.mapper;

import org.mapstruct.Mapper;

import com.examsphere.dto.request.StudentSignupRequest;
import com.examsphere.dto.request.TeacherCreationRequest;
import com.examsphere.dto.response.UserResponse;
import com.examsphere.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(StudentSignupRequest request);
    User toUser(TeacherCreationRequest request);
    UserResponse toUserResponse(User user);
}
