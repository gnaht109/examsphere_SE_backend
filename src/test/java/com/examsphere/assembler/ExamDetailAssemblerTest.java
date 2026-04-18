package com.examsphere.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.QuestionType;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.Passage;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.User;

class ExamDetailAssemblerTest {

    ExamDetailAssembler assembler;

    @BeforeEach
    void setUp() {
        ExamMapper examMapper = Mappers.getMapper(ExamMapper.class);
        assembler = new ExamDetailAssembler(examMapper);
    }

    @Test
    void toStudentDetailResponse_hidesCorrectFlags() {
        Exam exam = Exam.builder()
                .id(1L)
                .title("Mock Exam")
                .duration(30)
                .description("Desc")
                .status(ExamStatus.PUBLISHED)
                .createdBy(User.builder().id(5L).username("teacher").build())
                .build();

        Question standaloneQuestion = Question.builder()
                .id(10L)
                .content("Question")
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .exam(exam)
                .build();
        standaloneQuestion.getOptions().add(QuestionOption.builder()
                .id(100L)
                .content("A")
                .isCorrect(true)
                .question(standaloneQuestion)
                .build());

        Passage passage = Passage.builder()
                .id(20L)
                .content("Passage")
                .exam(exam)
                .build();

        Question passageQuestion = Question.builder()
                .id(11L)
                .content("Passage Question")
                .questionType(QuestionType.TRUE_FALSE)
                .exam(exam)
                .passage(passage)
                .build();
        passageQuestion.getOptions().add(QuestionOption.builder()
                .id(101L)
                .content("True")
                .isCorrect(false)
                .question(passageQuestion)
                .build());

        var response = assembler.toStudentDetailResponse(
                exam,
                List.of(standaloneQuestion),
                List.of(passage),
                Map.of(passage.getId(), List.of(passageQuestion))
        );

        assertThat(response.getQuestions()).hasSize(1);
        assertThat(response.getQuestions().getFirst().getOptions())
                .extracting("isCorrect")
                .containsExactly((Object) null);
        assertThat(response.getPassages()).hasSize(1);
        assertThat(response.getPassages().getFirst().getQuestions().getFirst().getOptions())
                .extracting("isCorrect")
                .containsExactly((Object) null);
    }
}
