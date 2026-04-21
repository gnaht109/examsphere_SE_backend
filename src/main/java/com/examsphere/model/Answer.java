package com.examsphere.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"session_id", "question_order"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    Session session;

    // use order instead of question_id
    @Column(name = "question_order", nullable = false)
    Integer questionOrder;

    @Column(columnDefinition = "TEXT")
    String answer;

    LocalDateTime answeredAt;

    @PrePersist
    void onCreate() {
        answeredAt = LocalDateTime.now();
    }
}