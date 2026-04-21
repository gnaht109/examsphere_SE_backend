package com.examsphere.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionListResponse {
    
    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("examTitle")
    private String examTitle;

    @JsonProperty("status")
    private String status; // IN_PROGRESS, SUBMITTED, GRADED

    @JsonProperty("startedAt")
    private LocalDateTime startedAt;

    @JsonProperty("submittedAt")
    private LocalDateTime submittedAt;

    @JsonProperty("score")
    private Double score;
}
