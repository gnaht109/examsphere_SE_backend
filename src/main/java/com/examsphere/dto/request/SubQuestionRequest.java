package com.examsphere.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
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
public class SubQuestionRequest {

    @NotBlank(message = "Sub-question content must not be blank")
    String content;

    @Positive(message = "Points must be positive")
    @Builder.Default
    Double points = 1.0;

    Integer subQuestionOrder;

    @NotEmpty(message = "Sub-question must have at least one option")
    @Valid
    List<QuestionOptionRequest> options;
}