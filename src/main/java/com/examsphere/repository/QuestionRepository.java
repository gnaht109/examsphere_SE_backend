package com.examsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.examsphere.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByIdAndExamCreatedById(Long questionId, Long userId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.exam.id = :examId
        ORDER BY CASE WHEN q.questionOrder IS NULL THEN 1 ELSE 0 END, q.questionOrder, q.id
    """)
    List<Question> findQuestionsWithOptions(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.exam.id = :examId AND q.passage IS NULL
        ORDER BY CASE WHEN q.questionOrder IS NULL THEN 1 ELSE 0 END, q.questionOrder, q.id
    """)
    List<Question> findStandaloneQuestions(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.passage.exam.id = :examId
        ORDER BY q.passage.id,
                 CASE WHEN q.questionOrder IS NULL THEN 1 ELSE 0 END,
                 q.questionOrder,
                 q.id
    """)
    List<Question> findPassageQuestionsByExamId(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.passage.id = :passageId
        ORDER BY CASE WHEN q.questionOrder IS NULL THEN 1 ELSE 0 END, q.questionOrder, q.id
    """)
    List<Question> findByPassageId(Long passageId);
}
