package com.examsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.service.ExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * ExamController — Teacher Side
 *
 * Auth note: teacher identity is currently passed via "X-User-Id" header.
 */
@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamController {

    ExamService examService;

    // ── Exam endpoints ────────────────────────────────────────────────────────

    // GET /api/exams — STUDENT: list all published exams
    @GetMapping("/exams")
    ApiResponse<List<ExamResponse>> getPublishedExams() {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(examService.getPublishedExams())
                .build();
    }

    // GET /api/exams/my — TEACHER: list their own exams
    @GetMapping("/exams/my")
    ApiResponse<List<ExamResponse>> getMyExams() {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(examService.getMyExams())
                .build();
    }

    // GET /api/exams/{id} — TEACHER/STUDENT: full exam with questions
    // Student calls this when starting an exam, so it must be accessible to both roles (but only if exam is PUBLISHED)
    @GetMapping("/exams/{examId}")
    ApiResponse<ExamResponse> getExamById(@PathVariable Long examId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.getExamById(examId))
                .build();
    }

    // POST /api/exams — TEACHER: create new exam (starts as DRAFT)
    @PostMapping("/exams")
    ApiResponse<ExamResponse> createExam(
            @Valid @RequestBody ExamRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.createExam(request))
                .build();
    }

    // PUT /api/exams/{id} — TEACHER (owner): update exam metadata
    @PutMapping("/exams/{examId}")
    ApiResponse<ExamResponse> updateExam(
            @PathVariable Long examId,
            @Valid @RequestBody ExamRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.updateExam(examId, request))
                .build();
    }

    // DELETE /api/exams/{id} — TEACHER (owner): delete exam
    @DeleteMapping("/exams/{examId}")
    ApiResponse<Void> deleteExam(
            @PathVariable Long examId) {
        examService.deleteExam(examId);
        return ApiResponse.<Void>builder()
                .message("Exam deleted successfully")
                .build();
    }

    // PUT /api/exams/{id}/publish — TEACHER: publish a DRAFT exam
    @PutMapping("/exams/{examId}/publish")
    ApiResponse<ExamResponse> publishExam(
            @PathVariable Long examId) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.publishExam(examId))
                .build();
    }

    // ── Question endpoints ────────────────────────────────────────────────────

    // POST /api/exams/{examId}/questions — TEACHER: add question to exam
    @PostMapping("/exams/{examId}/questions")
    ApiResponse<ExamResponse> addQuestion(
            @PathVariable Long examId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.addQuestion(examId, request))
                .build();
    }

    // PUT /api/questions/{questionId} — TEACHER: update a question
    @PutMapping("/exams/{examId}/questions/{questionId}")
    ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .data(examService.updateQuestion(questionId , request))
                .build();
    }

    // DELETE /api/questions/{id} — TEACHER: delete a question
    @DeleteMapping("/exams/{examId}/questions/{questionId}")
    ApiResponse<Void> deleteQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId
        ) {
        examService.deleteQuestion(questionId);
        return ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .build();
    }
}