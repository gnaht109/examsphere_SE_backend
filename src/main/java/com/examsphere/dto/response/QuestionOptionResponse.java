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
public class QuestionOptionResponse {

    Long id;
    String content;
    Boolean isCorrect;   // Member 5 (Grading) uses this for auto-grading
    Integer optionOrder;
}