package com.examsphere.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class QuestionOptionRequest {

    @NotBlank(message = "Option content must not be blank")
    String content;

    @Builder.Default
    Boolean isCorrect = false;

    Integer optionOrder;
}