package com.examsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examsphere.model.Answer;

public interface SessionAnswerRepository 
        extends JpaRepository<Answer, Long> {

    Optional<Answer> 
        findBySessionIdAndQuestionOrder(Long sessionId, Long questionOrder);
}