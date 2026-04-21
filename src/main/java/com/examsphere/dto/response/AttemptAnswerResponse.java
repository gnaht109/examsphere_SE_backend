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
public class AttemptAnswerResponse {

    Long attemptId;
    Long questionId;
    Long selectedOptionId;
    LocalDateTime answeredAt;
}
