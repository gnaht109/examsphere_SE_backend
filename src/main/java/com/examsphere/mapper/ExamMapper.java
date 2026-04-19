package com.examsphere.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.PassageRequest;
import com.examsphere.dto.request.QuestionOptionRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.PassageResponse;
import com.examsphere.dto.response.QuestionOptionResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.model.Exam;
import com.examsphere.model.Passage;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exam toExam(ExamRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateExam(ExamRequest request, @MappingTarget Exam exam);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Passage toPassage(PassageRequest request);

    @Mapping(target = "questions", ignore = true)
    PassageResponse toPassageResponse(Passage passage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "questions", ignore = true)
    void updatePassage(PassageRequest request, @MappingTarget Passage passage);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "questionCount",
            expression = "java(exam.getQuestions() != null ? exam.getQuestions().size() : 0)")
    ExamResponse toExamResponse(Exam exam);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "passages", ignore = true)
    ExamDetailResponse toDetailResponse(Exam exam);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    Question toQuestion(QuestionRequest request);

    @Mapping(target = "options", ignore = true)
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateQuestion(QuestionRequest request, @MappingTarget Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    QuestionOption toQuestionOption(QuestionOptionRequest request);

    QuestionOptionResponse toQuestionOptionResponse(QuestionOption option);

    List<QuestionOptionResponse> toQuestionOptionResponseList(List<QuestionOption> options);
}
