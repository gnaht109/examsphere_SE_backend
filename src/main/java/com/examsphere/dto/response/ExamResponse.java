package com.examsphere.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.examsphere.enums.ExamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ExamResponse {

    Long id;
    String title;
    String description;
    Integer duration;
    ExamStatus status;

    // Teacher info
    Long createdById;
    String createdByUsername;

    int questionCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // Populated for GET /api/exams/{id} (full detail)
    // Null for list view, populated for detail view
    List<QuestionResponse> questions;
}