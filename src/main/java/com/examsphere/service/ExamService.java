package com.examsphere.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.QuestionType;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.User;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.QuestionRepository;
import com.examsphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {

    ExamRepository examRepository;
    QuestionRepository questionRepository;
    UserRepository userRepository;
    AuthService authService;
    ExamMapper examMapper;

    public List<ExamResponse> getPublishedExams() {
        return examRepository.findByStatus(ExamStatus.PUBLISHED)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    public List<ExamResponse> getMyExams() {
        Long userId = authService.getCurrentUserId();

        return examRepository.findByCreatedById(userId)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = findExamOrThrow(id);
        return toDetailResponse(exam);
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        User teacher = authService.getCurrentUser();

        Exam exam = examMapper.toExam(request);
        exam.setCreatedBy(teacher);
        exam.setStatus(ExamStatus.DRAFT);

        System.out.println("1");

        return toDetailResponse(examRepository.save(exam));
        
    }

    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        examMapper.updateExam(request, exam);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public void deleteExam(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        examRepository.delete(exam);
    }

    @Transactional
    public ExamResponse publishExam(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        if (exam.getQuestions().isEmpty()) {
            throw new AppException(ErrorCode.EXAM_CANNOT_PUBLISH_EMPTY);
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse addQuestion(Long examId, QuestionRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(examId, userId);

        Question question = examMapper.toQuestion(request);
        question.setExam(exam);
        attachOptions(question, request);

        exam.getQuestions().add(question);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Long userId = authService.getCurrentUserId();

        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
            
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        examMapper.updateQuestion(request, question);
        question.getOptions().clear();
        attachOptions(question, request);

        return toQuestionResponse(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Long userId = authService.getCurrentUserId();

        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        questionRepository.deleteById(questionId);
    }

    Exam findExamOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
    }

    Exam findAndVerifyOwnership(Long id, Long userId) {
        Exam exam = findExamOrThrow(id);

        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        System.out.println("2");

        return exam;
    }

    void attachOptions(Question question, QuestionRequest request) {
        if (request.getOptions() != null) {
            request.getOptions().forEach(optReq -> {
                QuestionOption option = examMapper.toQuestionOption(optReq);
                option.setQuestion(question);
                question.getOptions().add(option);
            });
        }
    }

    ExamResponse toDetailResponse(Exam exam) {
        ExamResponse response = examMapper.toExamResponse(exam);
        response.setQuestions(
                exam.getQuestions().stream()
                        .map(this::toQuestionResponse)
                        .collect(Collectors.toList())
        );
        return response;
    }

    QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse qr = examMapper.toQuestionResponse(question);

        boolean hasOptions =
                question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                question.getQuestionType() == QuestionType.TRUE_FALSE;

        qr.setOptions(
                hasOptions
                        ? examMapper.toQuestionOptionResponseList(question.getOptions())
                        : null
        );

        return qr;
    }
}