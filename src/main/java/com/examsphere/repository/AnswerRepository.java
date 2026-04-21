package com.examsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examsphere.model.Answer;

public interface AnswerRepository 
        extends JpaRepository<Answer, Long> {

    // find one answer (autosave update)

    // get all answers of a session (for submit)
    List<Answer> findBySessionId(Long sessionId);

    // delete answers when restarting session
    void deleteBySessionId(Long sessionId);
    
    Optional<Answer> findBySessionIdAndQuestionOrder(
          Long sessionId,
          Integer questionOrder
    );

}