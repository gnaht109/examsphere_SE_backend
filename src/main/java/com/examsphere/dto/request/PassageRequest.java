package com.examsphere.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageRequest {

    String content;

    // optional: create passage with questions immediately
    List<QuestionRequest> questions;
}