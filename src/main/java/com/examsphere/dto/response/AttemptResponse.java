package com.examsphere.dto.response;

import java.time.LocalDateTime;

import com.examsphere.enums.AttemptStatus;

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
public class AttemptResponse {

    Long id;
    Long examId;
    String examTitle;
    Long studentId;
    AttemptStatus status;
    Integer durationMinutes;
    Double totalScore;
    LocalDateTime startedAt;
    LocalDateTime submittedAt;
    LocalDateTime expiresAt;
    Long remainingSeconds;
    Integer totalQuestions;
    Integer answeredQuestions;
    Double score;
}
