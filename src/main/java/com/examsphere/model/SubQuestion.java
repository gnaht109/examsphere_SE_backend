package com.examsphere.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * SubQuestion — belongs to a SHORT_ANSWER passage (Question).
 * Each sub-question is an MCQ item that students answer by selecting one option.
 * Fully auto-gradeable: correct answer is stored in SubQuestionOption.isCorrect.
 */
@Entity
@Table(name = "sub_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // The parent SHORT_ANSWER passage question
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @Builder.Default
    Double points = 1.0;

    Integer subQuestionOrder;

    // MCQ options for this sub-question
    @OneToMany(mappedBy = "subQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<SubQuestionOption> options = new ArrayList<>();
}