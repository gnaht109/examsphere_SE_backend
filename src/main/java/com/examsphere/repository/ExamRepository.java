package com.examsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.examsphere.enums.ExamStatus;
import com.examsphere.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // GET /api/exams — students see only PUBLISHED exams
    List<Exam> findByStatus(ExamStatus status);

    // GET /api/exams/my — teacher sees their own exams (all statuses)
    @Query("""
        SELECT e FROM Exam e
        JOIN FETCH e.createdBy
        WHERE e.createdBy.id = :userId
    """)
    List<Exam> findByCreatedById(Long userId);

    // Ownership check before PUT / DELETE
    boolean existsByIdAndCreatedById(Long examId, Long userId);

    @Query
    ("""
        SELECT DISTINCT e FROM Exam e
        JOIN FETCH e.createdBy
        LEFT JOIN FETCH e.questions q
        WHERE e.id = :id
    """)
    Optional<Exam> findDetailById( Long id);

    @Query("""
        SELECT e FROM Exam e
        JOIN FETCH e.createdBy
        WHERE e.createdBy.id = :userId
    """)
    List<Exam> findMyExams(Long userId);

    @Query("""
        SELECT e FROM Exam e
        LEFT JOIN FETCH e.createdBy
        WHERE e.id = :id
    """)
    Optional<Exam> findByIdBasic(Long id);

    @Query("""
        SELECT e FROM Exam e
        LEFT JOIN FETCH e.createdBy
        WHERE e.id = :id AND e.status = :status
    """)
    Optional<Exam> findByIdBasicAndStatus(Long id, ExamStatus status);


}
