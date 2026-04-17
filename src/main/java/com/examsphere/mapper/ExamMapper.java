package com.examsphere.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionOptionRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionOptionResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    // ─────────────────────────────────────────────
    // EXAM - CREATE / UPDATE
    // ─────────────────────────────────────────────

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

    // ─────────────────────────────────────────────
    // EXAM - LIST (ExamCard / lightweight)
    // ─────────────────────────────────────────────

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "questionCount",
            expression = "java(exam.getQuestions() != null ? exam.getQuestions().size() : 0)")
    ExamResponse toExamResponse(Exam exam);

    // ─────────────────────────────────────────────
    // EXAM - DETAIL (EditExamPage)
    // ─────────────────────────────────────────────

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "questions", ignore = true) // handled in service
    ExamDetailResponse toDetailResponse(Exam exam);

    // ─────────────────────────────────────────────
    // QUESTION
    // ─────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    Question toQuestion(QuestionRequest request);

    @Mapping(target = "options", ignore = true) // handled in service
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateQuestion(QuestionRequest request, @MappingTarget Question question);

    // ─────────────────────────────────────────────
    // QUESTION OPTION
    // ─────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    QuestionOption toQuestionOption(QuestionOptionRequest request);

    QuestionOptionResponse toQuestionOptionResponse(QuestionOption option);

    List<QuestionOptionResponse> toQuestionOptionResponseList(List<QuestionOption> options);
}