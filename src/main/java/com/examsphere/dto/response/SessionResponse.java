package com.examsphere.dto.response;

import java.time.LocalDateTime;

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
public class SessionResponse {

    private Long id;
    private String examTitle;
    private String examId;
    private Long durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Double score;
    private Long createdById;
    private String createdByUsername;

}