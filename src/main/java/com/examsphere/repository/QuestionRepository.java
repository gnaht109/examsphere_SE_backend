package com.examsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examsphere.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Ownership guard: question must belong to an exam owned by this teacher
    boolean existsByIdAndExamCreatedById(Long questionId, Long userId);
}