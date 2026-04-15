package com.examsphere.model;

import java.util.ArrayList;
import java.util.List;

import com.examsphere.enums.QuestionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Question — three types:
 *   MULTIPLE_CHOICE : content + options (QuestionOption list)
 *   TRUE_FALSE      : content + options (QuestionOption list, exactly 2)
 *   SHORT_ANSWER    : passage/topic in content + subQuestions (SubQuestion list),
 *                     each sub-question has its own MCQ options (SubQuestionOption)
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // For MULTIPLE_CHOICE / TRUE_FALSE: the question text
    // For SHORT_ANSWER: the reading passage / topic paragraph
    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    // Points for MULTIPLE_CHOICE / TRUE_FALSE
    // For SHORT_ANSWER: total points = sum of sub-question points (stored per sub-question)
    @Builder.Default
    Double points = 1.0;

    @Enumerated(EnumType.STRING)
    QuestionType questionType;

    Integer questionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    Exam exam;

    // Used by MULTIPLE_CHOICE and TRUE_FALSE only
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<QuestionOption> options = new ArrayList<>();

    // Used by SHORT_ANSWER only — each sub-question is an independent MCQ
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<SubQuestion> subQuestions = new ArrayList<>();
}