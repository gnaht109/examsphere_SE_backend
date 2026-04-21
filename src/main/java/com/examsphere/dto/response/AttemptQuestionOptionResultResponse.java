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
public class AttemptQuestionOptionResultResponse {

    Long optionId;
    String content;
    Integer optionOrder;
    Boolean correct;
    Boolean selected;
}
