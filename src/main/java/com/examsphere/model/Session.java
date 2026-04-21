package com.examsphere.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.SessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    Integer durationMinutes;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Double score;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User createdBy;

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = SessionStatus.IN_PROGRESS;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (submittedAt != null) {
            status = SessionStatus.SUBMITTED;
        }
    }
}