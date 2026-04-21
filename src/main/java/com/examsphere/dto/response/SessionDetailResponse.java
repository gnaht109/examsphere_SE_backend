package com.examsphere.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class SessionDetailResponse {

    Long sessionId;
    Long examId;
    Integer durationMinutes;
    LocalDateTime startedAt;

    List<QuestionResponse> questions;
}