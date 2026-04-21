package com.examsphere.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class AttemptResultResponse {

    Long attemptId;
    Long examId;
    String examTitle;
    Long studentId;
    AttemptStatus status;
    Double totalScore;
    Double score;
    Integer totalQuestions;
    Integer answeredQuestions;
    Integer correctAnswers;
    Integer incorrectAnswers;
    Integer unansweredQuestions;
    LocalDateTime startedAt;
    LocalDateTime submittedAt;
    List<AttemptQuestionResultResponse> questions;
}
