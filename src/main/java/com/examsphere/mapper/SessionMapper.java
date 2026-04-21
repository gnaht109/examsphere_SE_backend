package com.examsphere.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.Query;

import com.examsphere.dto.response.SessionDetailResponse;
import com.examsphere.dto.response.SessionResponse;
import com.examsphere.model.Question;
import com.examsphere.model.Session;

@Mapper(componentModel = "spring")
public interface SessionMapper {

   //  @Mapping(target = "sessionId", source = "id")
   //  @Mapping(target = "examTitle", source = "exam.title")
   //  @Mapping(target = "durationMinutes", source = "session.durationMinutes")
   //  @Mapping(target = "startedAt", source = "session.startedAt")
   //  SessionResponse toResponse(Session session);

    @Mapping(target = "examId", source = "exam.id")
    SessionResponse toResponse(Session session);
 
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "durationMinutes", source = "session.durationMinutes")
    @Mapping(target = "startedAt", source = "session.startedAt")
    @Mapping(target = "examId", source = "session.exam.id")
    @Mapping(target = "questions", source = "questions")
    SessionDetailResponse toDetailResponse(Session session, List<Question> questions);

}