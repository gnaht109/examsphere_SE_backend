package com.examsphere.dto.response;

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
public class SubQuestionOptionResponse {

    Long id;
    String content;
    Boolean isCorrect;  // Grading service uses this to auto-grade student answers
    Integer optionOrder;
}