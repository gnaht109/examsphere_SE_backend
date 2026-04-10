package com.examsphere.dto.response;

import java.util.List;

import com.examsphere.enums.QuestionType;

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
public class QuestionResponse {

    Long id;
    String content;
    Double points;
    QuestionType questionType;
    String explaination;
    Integer questionOrder;

    // Null for ESSAY / SHORT_ANSWER; populated for MULTIPLE_CHOICE / TRUE_FALSE
    List<QuestionOptionResponse> options;
}