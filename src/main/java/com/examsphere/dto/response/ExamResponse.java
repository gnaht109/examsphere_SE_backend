package com.examsphere.dto.response;

import java.time.LocalDateTime;

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
    Double totalScore;
    ExamStatus status;

    // Teacher info
    Long createdById;
    String createdByUsername;

    int questionCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}
