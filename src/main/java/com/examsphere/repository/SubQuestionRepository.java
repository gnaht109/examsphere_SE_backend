package com.examsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examsphere.model.SubQuestion;

@Repository
public interface SubQuestionRepository extends JpaRepository<SubQuestion, Long> {

    // Ownership guard: sub-question → question → exam → createdBy
    boolean existsByIdAndQuestionExamCreatedById(Long subQuestionId, Long userId);
}