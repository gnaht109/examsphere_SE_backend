package com.examsphere.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.assembler.ExamDetailAssembler;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.repository.ExamRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StudentExamService {

    ExamRepository examRepository;
    ExamMapper examMapper;
    ExamDetailAssembler examDetailAssembler;
    ExamDetailQueryService examDetailQueryService;

    @Transactional(readOnly = true)
    public List<ExamResponse> getPublishedExams() {
        return examRepository.findByStatus(ExamStatus.PUBLISHED)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDetailResponse getPublishedExamById(Long id) {
        Exam exam = examRepository.findByIdBasicAndStatus(id, ExamStatus.PUBLISHED)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_PUBLISHED));
        ExamDetailQueryData queryData = examDetailQueryService.loadExamDetailData(id);

        return examDetailAssembler.toStudentDetailResponse(
                exam,
                queryData.standaloneQuestions(),
                queryData.passages(),
                queryData.questionsByPassageId()
        );
    }
}
