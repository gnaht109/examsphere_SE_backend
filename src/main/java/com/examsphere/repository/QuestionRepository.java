package com.examsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.examsphere.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Ownership guard: question must belong to an exam owned by this teacher
    boolean existsByIdAndExamCreatedById(Long questionId, Long userId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.exam.id = :examId
    """)
    List<Question> findQuestionsWithOptions(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.exam.id = :examId AND q.passage IS NULL
    """)
    List<Question> findStandaloneQuestions(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.passage.exam.id = :examId
    """)
    List<Question> findPassageQuestionsByExamId(Long examId);

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN FETCH q.options
        WHERE q.passage.id = :passageId
    """)
    List<Question> findByPassageId(Long passageId);
}
