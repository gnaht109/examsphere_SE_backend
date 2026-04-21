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
public class AttemptQuestionResultResponse {

    Long questionId;
    Integer questionOrder;
    String content;
    Double points;
    Double earnedPoints;
    Boolean answered;
    Boolean correct;
    Long selectedOptionId;
    String selectedOptionContent;
    Long correctOptionId;
    String correctOptionContent;
}
