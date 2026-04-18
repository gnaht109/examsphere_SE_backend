package com.examsphere.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.assembler.ExamDetailAssembler;
import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.PassageRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.PassageResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.Passage;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.User;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.PassageRepository;
import com.examsphere.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TeacherExamService {

    ExamRepository examRepository;
    PassageRepository passageRepository;
    QuestionRepository questionRepository;
    AuthService authService;
    ExamMapper examMapper;
    ExamDetailAssembler examDetailAssembler;
    ExamDetailQueryService examDetailQueryService;

    @Transactional
    public ExamDetailResponse createExam(ExamRequest request) {
        User teacher = authService.getCurrentUser();

        Exam exam = examMapper.toExam(request);
        exam.setCreatedBy(teacher);
        exam.setStatus(ExamStatus.DRAFT);

        return examDetailAssembler.toTeacherDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamDetailResponse updateExam(Long id, ExamRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        examMapper.updateExam(request, exam);
        return examDetailAssembler.toTeacherDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public void deleteExam(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        examRepository.delete(exam);
    }

    @Transactional
    public ExamDetailResponse publishExam(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        if (exam.getQuestions().isEmpty()) {
            throw new AppException(ErrorCode.EXAM_CANNOT_PUBLISH_EMPTY);
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        return examDetailAssembler.toTeacherDetailResponse(examRepository.save(exam));
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getMyExams() {
        Long userId = authService.getCurrentUserId();

        return examRepository.findByCreatedById(userId)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDetailResponse getExamById(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);
        ExamDetailQueryData queryData = examDetailQueryService.loadExamDetailData(id);

        return examDetailAssembler.toTeacherDetailResponse(
                exam,
                queryData.standaloneQuestions(),
                queryData.passages(),
                queryData.questionsByPassageId()
        );
    }

    @Transactional
    public PassageResponse createPassage(Long examId, PassageRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(examId, userId);

        Passage passage = examMapper.toPassage(request);
        passage.setExam(exam);

        if (request.getQuestions() != null) {
            request.getQuestions().forEach(qReq -> {
                Question q = examMapper.toQuestion(qReq);
                q.setExam(exam);
                q.setPassage(passage);
                attachOptions(q, qReq);
                passage.getQuestions().add(q);
            });
        }

        exam.getPassages().add(passage);

        return examDetailAssembler.toTeacherPassageResponse(passageRepository.save(passage));
    }

    @Transactional
    public QuestionResponse addQuestionToPassage(Long passageId, QuestionRequest request) {
        Long userId = authService.getCurrentUserId();

        Passage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new AppException(ErrorCode.PASSAGE_NOT_FOUND));

        if (!passage.getExam().getCreatedBy().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Question question = examMapper.toQuestion(request);
        question.setExam(passage.getExam());
        question.setPassage(passage);
        attachOptions(question, request);

        passage.getQuestions().add(question);

        return examDetailAssembler.toTeacherQuestionResponse(questionRepository.save(question));
    }

    @Transactional
    public void deletePassage(Long passageId) {
        Long userId = authService.getCurrentUserId();

        Passage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new AppException(ErrorCode.PASSAGE_NOT_FOUND));

        if (!passage.getExam().getCreatedBy().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        passageRepository.delete(passage);
    }

    @Transactional
    public QuestionResponse addQuestion(Long examId, QuestionRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(examId, userId);

        Question question = examMapper.toQuestion(request);
        question.setExam(exam);
        attachOptions(question, request);

        exam.getQuestions().add(question);
        return examDetailAssembler.toTeacherQuestionResponse(questionRepository.save(question));
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

        return examDetailAssembler.toTeacherQuestionResponse(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Long userId = authService.getCurrentUserId();

        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        questionRepository.deleteById(questionId);
    }

    Exam findAndVerifyOwnership(Long id, Long userId) {
        Exam exam = examRepository.findOwnedDetailById(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

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

}
