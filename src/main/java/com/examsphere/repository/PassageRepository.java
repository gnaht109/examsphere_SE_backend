package com.examsphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.examsphere.model.Passage;

@Repository
public interface PassageRepository extends JpaRepository<Passage, Long>{
    List<Passage> findByExamId(Long examId);

    @Query("""
        SELECT p FROM Passage p
        WHERE p.exam.id = :examId
    """)
    List<Passage> findPassages(Long examId);
}
