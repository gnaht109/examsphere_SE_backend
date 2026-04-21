package com.examsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examsphere.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
   List<Session> findByStudentId(Long studentId);
   Optional<Session> findById(Long id);

   
}