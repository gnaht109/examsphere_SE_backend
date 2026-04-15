package com.examsphere.dto.response;

import java.util.List;

import com.examsphere.enums.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * QuestionResponse
 *
 * MULTIPLE_CHOICE / TRUE_FALSE : options populated, subQuestions null
 * SHORT_ANSWER                 : subQuestions populated (each with its own options),
 *                                options null, content is the passage text
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class QuestionResponse {

    Long id;
    String content;
    Double points;
    QuestionType questionType;
    Integer questionOrder;

    // MULTIPLE_CHOICE / TRUE_FALSE only
    List<QuestionOptionResponse> options;

    // SHORT_ANSWER only — each sub-question is a full MCQ item
    List<SubQuestionResponse> subQuestions;
}