package com.examsphere.dto.request;

import lombok.Data;

@Data
public class AnswerRequest {
    private Integer questionOrder;
    private String answer;
}