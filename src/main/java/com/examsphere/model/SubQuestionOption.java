package com.examsphere.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * SubQuestionOption — one MCQ choice for a SubQuestion.
 * isCorrect is used by the grading service to auto-grade student answers.
 */
@Entity
@Table(name = "sub_question_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubQuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_question_id", nullable = false)
    SubQuestion subQuestion;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    // Grading service reads this to auto-grade student answers
    @Builder.Default
    Boolean isCorrect = false;

    Integer optionOrder;
}