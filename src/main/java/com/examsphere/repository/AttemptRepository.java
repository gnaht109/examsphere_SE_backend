package com.examsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examsphere.enums.AttemptStatus;
import com.examsphere.model.Attempt;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    Optional<Attempt> findByIdAndStudentId(Long attemptId, Long studentId);

    List<Attempt> findAllByExamIdAndStudentIdOrderByStartedAtDescIdDesc(Long examId, Long studentId);

    List<Attempt> findByStudentId(Long studentId);

    List<Attempt> findByExamId(Long examId);

    Optional<Attempt> findByExamIdAndStudentIdAndStatus(Long examId, Long studentId, AttemptStatus status);
}
