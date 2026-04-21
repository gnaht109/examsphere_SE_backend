package com.examsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.examsphere.model.AttemptAnswer;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    void deleteByAttemptId(Long attemptId);

    List<AttemptAnswer> findByAttemptId(Long attemptId);

    Optional<AttemptAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    long countByAttemptId(Long attemptId);

    @Query("""
        SELECT aa FROM AttemptAnswer aa
        JOIN FETCH aa.question q
        LEFT JOIN FETCH aa.selectedOption
        WHERE aa.attempt.id = :attemptId
        ORDER BY CASE WHEN q.questionOrder IS NULL THEN 1 ELSE 0 END, q.questionOrder, q.id
    """)
    List<AttemptAnswer> findDetailedByAttemptId(Long attemptId);
}
