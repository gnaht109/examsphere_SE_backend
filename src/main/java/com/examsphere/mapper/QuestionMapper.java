package com.examsphere.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.model.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "examId", source = "exam.id")
    QuestionResponse toQuestionResponse(Question question);

}