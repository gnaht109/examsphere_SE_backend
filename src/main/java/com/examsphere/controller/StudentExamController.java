package com.examsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.service.StudentExamService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StudentExamController {

    StudentExamService studentExamService;

    @GetMapping("/exams")
    ApiResponse<List<ExamResponse>> getPublishedExams() {
        return ApiResponse.<List<ExamResponse>>builder()
                .data(studentExamService.getPublishedExams())
                .build();
    }

    @GetMapping("/exams/{examId}")
    ApiResponse<ExamDetailResponse> getPublishedExamById(@PathVariable Long examId) {
        return ApiResponse.<ExamDetailResponse>builder()
                .data(studentExamService.getPublishedExamById(examId))
                .build();
    }
}
