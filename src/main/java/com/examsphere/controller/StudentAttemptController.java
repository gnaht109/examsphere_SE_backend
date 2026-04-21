package com.examsphere.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examsphere.dto.request.AttemptAnswerRequest;
import com.examsphere.dto.response.AttemptAnswerResponse;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.AttemptResponse;
import com.examsphere.dto.response.AttemptResultResponse;
import com.examsphere.enums.AttemptStatus;
import com.examsphere.service.StudentAttemptService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StudentAttemptController {

    StudentAttemptService studentAttemptService;

    @PostMapping("/exams/{examId}/attempts")
    ApiResponse<AttemptResponse> startAttempt(@PathVariable Long examId) {
        return ApiResponse.<AttemptResponse>builder()
                .data(studentAttemptService.startAttempt(examId))
                .build();
    }

    @GetMapping("/attempts/{attemptId}")
    ApiResponse<AttemptResponse> getAttempt(@PathVariable Long attemptId) {
        return ApiResponse.<AttemptResponse>builder()
                .data(studentAttemptService.getAttempt(attemptId))
                .build();
    }

    @GetMapping("/attempts")
    ApiResponse<java.util.List<AttemptResponse>> getAttempts(@RequestParam(required = false) AttemptStatus status) {
        return ApiResponse.<java.util.List<AttemptResponse>>builder()
                .data(studentAttemptService.getAttempts(status))
                .build();
    }

    @PostMapping("/attempts/{attemptId}/answers")
    ApiResponse<AttemptAnswerResponse> saveAnswer(@PathVariable Long attemptId,
            @RequestBody @Valid AttemptAnswerRequest request) {
        return ApiResponse.<AttemptAnswerResponse>builder()
                .data(studentAttemptService.saveAnswer(attemptId, request))
                .build();
    }

    @PostMapping("/attempts/{attemptId}/submit")
    ApiResponse<AttemptResponse> submitAttempt(@PathVariable Long attemptId) {
        return ApiResponse.<AttemptResponse>builder()
                .data(studentAttemptService.submitAttempt(attemptId))
                .build();
    }

    @GetMapping("/attempts/{attemptId}/result")
    ApiResponse<AttemptResultResponse> getResult(@PathVariable Long attemptId) {
        return ApiResponse.<AttemptResultResponse>builder()
                .data(studentAttemptService.getResult(attemptId))
                .build();
    }
}
