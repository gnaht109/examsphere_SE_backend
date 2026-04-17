package com.examsphere.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.PassageRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.PassageResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.QuestionType;
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
import com.examsphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {

    ExamRepository examRepository;
    PassageRepository passageRepository;
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

    @Transactional
    public List<ExamResponse> getMyExams() {
        Long userId = authService.getCurrentUserId();

        return examRepository.findByCreatedById(userId)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExamDetailResponse getExamById(Long id) {
        Exam exam = examRepository.findByIdBasic(id)
            .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        return buildExamDetailResponse(exam, false);
    }

    @Transactional
    public ExamDetailResponse getPublishedExamById(Long id) {
        Exam exam = examRepository.findByIdBasicAndStatus(id, ExamStatus.PUBLISHED)
            .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_PUBLISHED));

        return buildExamDetailResponse(exam, true);
    }

    @Transactional
    public ExamDetailResponse createExam(ExamRequest request) {
        User teacher = authService.getCurrentUser();

        Exam exam = examMapper.toExam(request);
        exam.setCreatedBy(teacher);
        exam.setStatus(ExamStatus.DRAFT);

        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamDetailResponse updateExam(Long id, ExamRequest request) {
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
    public ExamDetailResponse publishExam(Long id) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(id, userId);

        if (exam.getQuestions().isEmpty()) {
            throw new AppException(ErrorCode.EXAM_CANNOT_PUBLISH_EMPTY);
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        return toDetailResponse(examRepository.save(exam));
    }

    //---------------------------------------------------------------------//

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

        return toPassageResponse(passageRepository.save(passage));
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

        return toQuestionResponse(questionRepository.save(question));
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

    //---------------------------------------------------------------------//

    @Transactional
    public QuestionResponse addQuestion(Long examId, QuestionRequest request) {
        Long userId = authService.getCurrentUserId();
        Exam exam = findAndVerifyOwnership(examId, userId);

        Question question = examMapper.toQuestion(request);
        question.setExam(exam);
        attachOptions(question, request);

        exam.getQuestions().add(question);
        return toQuestionResponse(questionRepository.save(question));
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

    Exam findAndVerifyOwnership(Long id, Long userId) {
        Exam exam = examRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

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

    ExamDetailResponse buildExamDetailResponse(Exam exam, boolean hideAnswers) {
        List<Question> standalone = questionRepository.findStandaloneQuestions(exam.getId());
        List<Passage> passages = passageRepository.findPassages(exam.getId());

        ExamDetailResponse response = examMapper.toDetailResponse(exam);
        response.setQuestions(
                standalone.stream()
                        .map(question -> hideAnswers
                                ? toStudentQuestionResponse(question)
                                : toQuestionResponse(question))
                        .collect(Collectors.toList())
        );

        response.setPassages(
                passages.stream().map(passage -> {
                    PassageResponse pr = new PassageResponse();
                    pr.setId(passage.getId());
                    pr.setContent(passage.getContent());

                    List<Question> passageQuestions =
                            questionRepository.findByPassageId(passage.getId());

                    pr.setQuestions(
                            passageQuestions.stream()
                                    .map(question -> hideAnswers
                                            ? toStudentQuestionResponse(question)
                                            : toQuestionResponse(question))
                                    .collect(Collectors.toList())
                    );

                    return pr;
                }).collect(Collectors.toList())
        );

        return response;
    }

    ExamDetailResponse toDetailResponse(Exam exam) {
        ExamDetailResponse response = examMapper.toDetailResponse(exam);
        response.setQuestions(
                exam.getQuestions().stream()
                        .map(this::toQuestionResponse)
                        .collect(Collectors.toList())
        );

        response.setPassages(
                exam.getPassages().stream()
                        .map(this::toPassageResponse)
                        .collect(Collectors.toList())
        );

        return response;
    }

    PassageResponse toPassageResponse(Passage passage) {
        PassageResponse res = examMapper.toPassageResponse(passage);

        res.setQuestions(
                passage.getQuestions().stream()
                        .map(this::toQuestionResponse)
                        .toList()
        );

        return res;
    }

    QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse qr = examMapper.toQuestionResponse(question);

        boolean hasOptions =
                question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                question.getQuestionType() == QuestionType.TRUE_FALSE;

        qr.setOptions(
                hasOptions
                        ? examMapper.toQuestionOptionResponseList(
                            new ArrayList<>(question.getOptions())
                        )
                        : null
        );

        return qr;
    }

    QuestionResponse toStudentQuestionResponse(Question question) {
        QuestionResponse qr = toQuestionResponse(question);

        if (qr.getOptions() != null) {
            qr.getOptions().forEach(option -> option.setIsCorrect(null));
        }

        return qr;
    }
}
