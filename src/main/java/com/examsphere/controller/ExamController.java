package com.examsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.request.SubQuestionRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.dto.response.SubQuestionResponse;
import com.examsphere.service.ExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamController {

    ExamService examService;

    // ── Exam endpoints ────────────────────────────────────────────────────────

    @GetMapping("/exams")
    ApiResponse<List<ExamResponse>> getPublishedExams() {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(examService.getPublishedExams())
                .build();
    }

    @GetMapping("/exams/my")
    ApiResponse<List<ExamResponse>> getMyExams(
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(examService.getMyExams(userId))
                .build();
    }

    @GetMapping("/exams/{id}")
    ApiResponse<ExamResponse> getExamById(@PathVariable Long id) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.getExamById(id))
                .build();
    }

    @PostMapping("/exams")
    ApiResponse<ExamResponse> createExam(
            @Valid @RequestBody ExamRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.createExam(request, userId))
                .build();
    }

    @PutMapping("/exams/{id}")
    ApiResponse<ExamResponse> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.updateExam(id, request, userId))
                .build();
    }

    @DeleteMapping("/exams/{id}")
    ApiResponse<Void> deleteExam(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        examService.deleteExam(id, userId);
        return ApiResponse.<Void>builder()
                .message("Exam deleted successfully")
                .build();
    }

    @PutMapping("/exams/{id}/publish")
    ApiResponse<ExamResponse> publishExam(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.publishExam(id, userId))
                .build();
    }

    // ── Question endpoints ────────────────────────────────────────────────────

    // Works for all 3 types: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    @PostMapping("/exams/{id}/questions")
    ApiResponse<ExamResponse> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.addQuestion(id, request, userId))
                .build();
    }

    @PutMapping("/questions/{id}")
    ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<QuestionResponse>builder()
                .data(examService.updateQuestion(id, request, userId))
                .build();
    }

    @DeleteMapping("/questions/{id}")
    ApiResponse<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        examService.deleteQuestion(id, userId);
        return ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .build();
    }

    // ── Sub-Question endpoints (SHORT_ANSWER only) ────────────────────────────

    // POST /api/questions/{id}/sub-questions — add a sub-question to a SHORT_ANSWER passage
    @PostMapping("/questions/{id}/sub-questions")
    ApiResponse<QuestionResponse> addSubQuestion(
            @PathVariable Long id,
            @Valid @RequestBody SubQuestionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<QuestionResponse>builder()
                .data(examService.addSubQuestion(id, request, userId))
                .build();
    }

    // PUT /api/sub-questions/{id} — update a sub-question
    @PutMapping("/sub-questions/{id}")
    ApiResponse<SubQuestionResponse> updateSubQuestion(
            @PathVariable Long id,
            @Valid @RequestBody SubQuestionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.<SubQuestionResponse>builder()
                .data(examService.updateSubQuestion(id, request, userId))
                .build();
    }

    // DELETE /api/sub-questions/{id} — delete a sub-question
    @DeleteMapping("/sub-questions/{id}")
    ApiResponse<Void> deleteSubQuestion(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        examService.deleteSubQuestion(id, userId);
        return ApiResponse.<Void>builder()
                .message("Sub-question deleted successfully")
                .build();
    }
}