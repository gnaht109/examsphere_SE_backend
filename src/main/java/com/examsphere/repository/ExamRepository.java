package com.examsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examsphere.enums.ExamStatus;
import com.examsphere.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // GET /api/exams — students see only PUBLISHED exams
    List<Exam> findByStatus(ExamStatus status);

    // GET /api/exams/my — teacher sees their own exams (all statuses)
    List<Exam> findByCreatedById(Long userId);

    // Ownership check before PUT / DELETE
    boolean existsByIdAndCreatedById(Long examId, Long userId);
}