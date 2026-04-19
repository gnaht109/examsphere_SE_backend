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
import com.examsphere.dto.request.PassageRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.PassageResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.service.TeacherExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TeacherExamController {

    TeacherExamService teacherExamService;

    @GetMapping("/exams/my")
    ApiResponse<List<ExamResponse>> getMyExams() {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(teacherExamService.getMyExams())
                .build();
    }

    @GetMapping("/exams/{examId}")
    ApiResponse<ExamDetailResponse> getExamById(@PathVariable Long examId) {
        return ApiResponse.<ExamDetailResponse>builder()
                .data(teacherExamService.getExamById(examId))
                .build();
    }

    @PostMapping("/exams")
    ApiResponse<ExamDetailResponse> createExam(@Valid @RequestBody ExamRequest request) {
        return ApiResponse.<ExamDetailResponse>builder()
                .data(teacherExamService.createExam(request))
                .build();
    }

    @PutMapping("/exams/{examId}")
    ApiResponse<ExamDetailResponse> updateExam(
            @PathVariable Long examId,
            @Valid @RequestBody ExamRequest request) {
        return ApiResponse.<ExamDetailResponse>builder()
                .data(teacherExamService.updateExam(examId, request))
                .build();
    }

    @DeleteMapping("/exams/{examId}")
    ApiResponse<Void> deleteExam(@PathVariable Long examId) {
        teacherExamService.deleteExam(examId);
        return ApiResponse.<Void>builder()
                .message("Exam deleted successfully")
                .build();
    }

    @PutMapping("/exams/{examId}/publish")
    ApiResponse<ExamDetailResponse> publishExam(@PathVariable Long examId) {
        return ApiResponse.<ExamDetailResponse>builder()
                .data(teacherExamService.publishExam(examId))
                .build();
    }

    @PostMapping("/exams/{examId}/questions")
    ApiResponse<QuestionResponse> addQuestion(
            @PathVariable Long examId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .data(teacherExamService.addQuestion(examId, request))
                .build();
    }

    @PutMapping("/exams/{examId}/questions/{questionId}")
    ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .data(teacherExamService.updateQuestion(questionId, request))
                .build();
    }

    @DeleteMapping("/exams/{examId}/questions/{questionId}")
    ApiResponse<Void> deleteQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId) {
        teacherExamService.deleteQuestion(questionId);
        return ApiResponse.<Void>builder()
                .message("Question deleted successfully")
                .build();
    }

    @PostMapping("/exams/{examId}/passages")
    ApiResponse<PassageResponse> createPassage(
            @PathVariable Long examId,
            @Valid @RequestBody PassageRequest request) {
        return ApiResponse.<PassageResponse>builder()
                .data(teacherExamService.createPassage(examId, request))
                .build();
    }

    @PutMapping("/passages/{passageId}")
    ApiResponse<PassageResponse> updatePassage(
            @PathVariable Long passageId,
            @Valid @RequestBody PassageRequest request) {
        return ApiResponse.<PassageResponse>builder()
                .data(teacherExamService.updatePassage(passageId, request))
                .build();
    }

    @PostMapping("/passages/{passageId}/questions")
    ApiResponse<QuestionResponse> addQuestionToPassage(
            @PathVariable Long passageId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .data(teacherExamService.addQuestionToPassage(passageId, request))
                .build();
    }

    @DeleteMapping("/passages/{passageId}")
    ApiResponse<Void> deletePassage(@PathVariable Long passageId) {
        teacherExamService.deletePassage(passageId);
        return ApiResponse.<Void>builder()
                .message("Passage deleted successfully")
                .build();
    }
}
