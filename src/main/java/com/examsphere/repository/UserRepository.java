package com.examsphere.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.examsphere.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Giữ nguyên kiểu trả về User như nhóm đang để
    User findByEmail(String email); 
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Giữ lại Optional cho Username nếu nhóm đã để thế này
    Optional<User> findByUsername(String username);
}
