package com.examsphere.dto.request;

import java.util.List;

import com.examsphere.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class QuestionRequest {

    @NotBlank(message = "Content must not be blank")
    String content;

    @Positive(message = "Points must be positive")
    Double points;

    @NotNull(message = "Question type must not be null")
    @JsonProperty("type")
    QuestionType questionType;

    String explaination;

    Integer questionOrder;

    // Required for MULTIPLE_CHOICE / TRUE_FALSE; ignored for ESSAY / SHORT_ANSWER
    @Valid
    List<QuestionOptionRequest> options;
}
