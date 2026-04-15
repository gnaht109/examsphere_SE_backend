package com.examsphere.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionOptionRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.request.SubQuestionRequest;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionOptionResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.dto.response.SubQuestionOptionResponse;
import com.examsphere.dto.response.SubQuestionResponse;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.SubQuestion;
import com.examsphere.model.SubQuestionOption;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    // ── Exam ──────────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exam toExam(ExamRequest request);

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "questionCount", expression = "java(exam.getQuestions().size())")
    @Mapping(target = "questions", ignore = true)
    ExamResponse toExamResponse(Exam exam);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateExam(ExamRequest request, @MappingTarget Exam exam);

    // ── Question ──────────────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "subQuestions", ignore = true)
    Question toQuestion(QuestionRequest request);

    @Mapping(target = "options", ignore = true)
    @Mapping(target = "subQuestions", ignore = true)
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "subQuestions", ignore = true)
    void updateQuestion(QuestionRequest request, @MappingTarget Question question);

    // ── QuestionOption (for MULTIPLE_CHOICE / TRUE_FALSE) ────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    QuestionOption toQuestionOption(QuestionOptionRequest request);

    QuestionOptionResponse toQuestionOptionResponse(QuestionOption option);

    List<QuestionOptionResponse> toQuestionOptionResponseList(List<QuestionOption> options);

    // ── SubQuestion (for SHORT_ANSWER) ────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "options", ignore = true)
    SubQuestion toSubQuestion(SubQuestionRequest request);

    @Mapping(target = "options", ignore = true)
    SubQuestionResponse toSubQuestionResponse(SubQuestion subQuestion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateSubQuestion(SubQuestionRequest request, @MappingTarget SubQuestion subQuestion);

    // ── SubQuestionOption ─────────────────────────────────────────────────────

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subQuestion", ignore = true)
    SubQuestionOption toSubQuestionOption(QuestionOptionRequest request);

    SubQuestionOptionResponse toSubQuestionOptionResponse(SubQuestionOption option);

    List<SubQuestionOptionResponse> toSubQuestionOptionResponseList(List<SubQuestionOption> options);
}