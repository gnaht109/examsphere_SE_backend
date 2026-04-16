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
//     @GetMapping("/exams/my")
//     ApiResponse<List<ExamResponse>> getMyExams {
//         return ApiResponse.<List<ExamResponse>>builder()
//                 .data(examService.getMyExams())
//                 .build();
//     }

    // GET /api/exams/{id} — TEACHER/STUDENT: full exam with questions
    // Student calls this when starting an exam, so it must be accessible to both roles (but only if exam is PUBLISHED)
    @GetMapping("/exams/{id}")
    ApiResponse<ExamResponse> getExamById(@PathVariable Long id) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.getExamById(id))
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
    @PutMapping("/exams/{id}")
    ApiResponse<ExamResponse> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.updateExam(id, request))
                .build();
    }

    // DELETE /api/exams/{id} — TEACHER (owner): delete exam
    @DeleteMapping("/exams/{id}")
    ApiResponse<Void> deleteExam(
            @PathVariable Long id) {
        examService.deleteExam(id);
        return ApiResponse.<Void>builder()
                .message("Exam deleted successfully")
                .build();
    }

    // PUT /api/exams/{id}/publish — TEACHER: publish a DRAFT exam
    @PutMapping("/exams/{id}/publish")
    ApiResponse<ExamResponse> publishExam(
            @PathVariable Long id) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.publishExam(id))
                .build();
    }

    // ── Question endpoints ────────────────────────────────────────────────────

    // POST /api/exams/{id}/questions — TEACHER: add question to exam
    @PostMapping("/exams/{id}/questions")
    ApiResponse<ExamResponse> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .data(examService.addQuestion(id, request))
                .build();
    }

    // PUT /api/questions/{id} — TEACHER: update a question
    @PutMapping("/questions/{id}")
    ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .data(examService.updateQuestion(id, request))
                .build();
    }

    // DELETE /api/questions/{id} — TEACHER: delete a question
    @DeleteMapping("/questions/{id}")
    ApiResponse<Void> deleteQuestion(
            @PathVariable Long id) {
        examService.deleteQuestion(id);
        return ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .build();
    }
}