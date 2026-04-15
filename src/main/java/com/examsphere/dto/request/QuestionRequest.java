package com.examsphere.dto.request;

import java.util.List;

import com.examsphere.enums.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * QuestionRequest
 *
 * MULTIPLE_CHOICE : fill content + points + options (>= 2, exactly 1 isCorrect=true)
 * TRUE_FALSE      : fill content + points + options (exactly 2: "True" / "False")
 * SHORT_ANSWER    : fill content (the passage/topic) + subQuestions (>= 1 sub-question,
 *                   each sub-question has its own MCQ options)
 *                   → points field is ignored; total points = sum of subQuestion.points
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class QuestionRequest {

    @NotBlank(message = "Content must not be blank")
    String content;

    // Used for MULTIPLE_CHOICE and TRUE_FALSE only
    @Positive(message = "Points must be positive")
    @Builder.Default
    Double points = 1.0;

    @NotNull(message = "Question type must not be null")
    QuestionType questionType;

    Integer questionOrder;

    // For MULTIPLE_CHOICE and TRUE_FALSE
    @Valid
    List<QuestionOptionRequest> options;

    // For SHORT_ANSWER only — the MCQ sub-questions under the passage
    @Valid
    List<SubQuestionRequest> subQuestions;
}