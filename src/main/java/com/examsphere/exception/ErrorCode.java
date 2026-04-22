package com.examsphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCAUGHT_ERROR(9999, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    // User & auth — 1000s
    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(1004, "Incorrect password", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1005, "Invalid input", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission to access", HttpStatus.FORBIDDEN),

    // Exam — 2000s
    EXAM_NOT_FOUND(2001, "Exam not found", HttpStatus.NOT_FOUND),
    EXAM_ALREADY_CLOSED(2002, "Exam is already closed", HttpStatus.BAD_REQUEST),
    EXAM_NOT_PUBLISHED(2003, "Exam is not published yet", HttpStatus.BAD_REQUEST),
    EXAM_CANNOT_PUBLISH_EMPTY(2004, "Cannot publish an exam with no questions", HttpStatus.BAD_REQUEST),
    EXAM_INVALID_TOTAL_SCORE(2005, "Exam question points must match the total score", HttpStatus.BAD_REQUEST),
    EXAM_HAS_ATTEMPTS(2006, "Cannot delete exam with existing attempts", HttpStatus.CONFLICT),
    
    // Question — 3000s
    QUESTION_NOT_FOUND(3001, "Question not found", HttpStatus.NOT_FOUND),
    QUESTION_OPTION_NOT_FOUND(3002, "Question option not found", HttpStatus.NOT_FOUND),

    // Passage -- 3500s
    PASSAGE_NOT_FOUND(3501, "Passage not found", HttpStatus.NOT_FOUND),

    // Submission — 4000s
    SUBMISSION_NOT_FOUND(4001, "Submission not found", HttpStatus.NOT_FOUND),
    SUBMISSION_ALREADY_SUBMITTED(4002, "Exam already submitted", HttpStatus.BAD_REQUEST),
    SUBMISSION_EXPIRED(4003, "Exam time has expired", HttpStatus.BAD_REQUEST),
    ATTEMPT_NOT_FOUND(4004, "Attempt not found", HttpStatus.NOT_FOUND),
    ATTEMPT_NOT_FINISHED(4005, "Attempt is still in progress", HttpStatus.BAD_REQUEST),

    // Result — 5000s
    RESULT_NOT_FOUND(5001, "Result not found", HttpStatus.NOT_FOUND),
 

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    
}
