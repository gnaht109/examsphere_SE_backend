package com.examsphere.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examsphere.assembler.ExamDetailAssembler;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.User;
import com.examsphere.repository.AttemptRepository;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.PassageRepository;
import com.examsphere.repository.QuestionRepository;

@ExtendWith(MockitoExtension.class)
class TeacherExamServiceTest {

    @Mock
    ExamRepository examRepository;

    @Mock
    AttemptRepository attemptRepository;

    @Mock
    PassageRepository passageRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    AuthService authService;

    @Mock
    ExamMapper examMapper;

    @Mock
    ExamDetailAssembler examDetailAssembler;

    @Mock
    ExamDetailQueryService examDetailQueryService;

    TeacherExamService teacherExamService;

    @BeforeEach
    void setUp() {
        teacherExamService = new TeacherExamService(
                examRepository,
                attemptRepository,
                passageRepository,
                questionRepository,
                authService,
                examMapper,
                examDetailAssembler,
                examDetailQueryService
        );
    }

    @Test
    void deleteExam_whenAttemptsExist_deletesOwnedExam() {
        Long examId = 10L;
        Long teacherId = 3L;
        Exam exam = Exam.builder()
                .id(examId)
                .createdBy(User.builder().id(teacherId).build())
                .build();

        when(authService.getCurrentUserId()).thenReturn(teacherId);
        when(examRepository.findOwnedDetailById(examId, teacherId)).thenReturn(Optional.of(exam));
        teacherExamService.deleteExam(examId);

        verify(examRepository).delete(exam);
    }
}
