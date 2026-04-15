package com.examsphere.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.dto.request.ExamRequest;
import com.examsphere.dto.request.QuestionRequest;
import com.examsphere.dto.request.SubQuestionRequest;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.dto.response.SubQuestionResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.QuestionType;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.SubQuestion;
import com.examsphere.model.SubQuestionOption;
import com.examsphere.model.User;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.QuestionRepository;
import com.examsphere.repository.SubQuestionRepository;
import com.examsphere.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {

    ExamRepository examRepository;
    QuestionRepository questionRepository;
    SubQuestionRepository subQuestionRepository;
    UserRepository userRepository;
    ExamMapper examMapper;

    // ── Exam CRUD ─────────────────────────────────────────────────────────────

    public List<ExamResponse> getPublishedExams() {
        return examRepository.findByStatus(ExamStatus.PUBLISHED)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    public List<ExamResponse> getMyExams(Long userId) {
        return examRepository.findByCreatedById(userId)
                .stream()
                .map(examMapper::toExamResponse)
                .collect(Collectors.toList());
    }

    public ExamResponse getExamById(Long id) {
        return toDetailResponse(findExamOrThrow(id));
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request, Long userId) {
        User teacher = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Exam exam = examMapper.toExam(request);
        exam.setCreatedBy(teacher);
        exam.setStatus(ExamStatus.DRAFT);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request, Long userId) {
        Exam exam = findAndVerifyOwnership(id, userId);
        examMapper.updateExam(request, exam);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public void deleteExam(Long id, Long userId) {
        examRepository.delete(findAndVerifyOwnership(id, userId));
    }

    @Transactional
    public ExamResponse publishExam(Long id, Long userId) {
        Exam exam = findAndVerifyOwnership(id, userId);
        if (exam.getQuestions().isEmpty()) {
            throw new AppException(ErrorCode.EXAM_CANNOT_PUBLISH_EMPTY);
        }
        exam.setStatus(ExamStatus.PUBLISHED);
        return toDetailResponse(examRepository.save(exam));
    }

    // ── Question Management ───────────────────────────────────────────────────

    @Transactional
    public ExamResponse addQuestion(Long examId, QuestionRequest request, Long userId) {
        Exam exam = findAndVerifyOwnership(examId, userId);
        validateQuestionRequest(request);

        Question question = examMapper.toQuestion(request);
        question.setExam(exam);
        attachOptionsOrSubQuestions(question, request);

        exam.getQuestions().add(question);
        return toDetailResponse(examRepository.save(exam));
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request, Long userId) {
        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        validateQuestionRequest(request);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        examMapper.updateQuestion(request, question);
        question.getOptions().clear();
        question.getSubQuestions().clear();
        attachOptionsOrSubQuestions(question, request);

        return toQuestionResponse(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long questionId, Long userId) {
        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        questionRepository.deleteById(questionId);
    }

    // ── Sub-Question Management (SHORT_ANSWER only) ───────────────────────────

    // POST /api/questions/{id}/sub-questions — add a sub-question to a SHORT_ANSWER passage
    @Transactional
    public QuestionResponse addSubQuestion(Long questionId, SubQuestionRequest request, Long userId) {
        if (!questionRepository.existsByIdAndExamCreatedById(questionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.getQuestionType() != QuestionType.SHORT_ANSWER) {
            throw new AppException(ErrorCode.INVALID_QUESTION_TYPE);
        }

        SubQuestion subQuestion = buildSubQuestion(request, question);
        question.getSubQuestions().add(subQuestion);
        return toQuestionResponse(questionRepository.save(question));
    }

    // PUT /api/sub-questions/{id} — update a sub-question
    @Transactional
    public SubQuestionResponse updateSubQuestion(Long subQuestionId, SubQuestionRequest request, Long userId) {
        if (!subQuestionRepository.existsByIdAndQuestionExamCreatedById(subQuestionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        SubQuestion subQuestion = subQuestionRepository.findById(subQuestionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        examMapper.updateSubQuestion(request, subQuestion);
        subQuestion.getOptions().clear();
        attachSubQuestionOptions(subQuestion, request);

        return toSubQuestionResponse(subQuestionRepository.save(subQuestion));
    }

    // DELETE /api/sub-questions/{id} — delete a sub-question
    @Transactional
    public void deleteSubQuestion(Long subQuestionId, Long userId) {
        if (!subQuestionRepository.existsByIdAndQuestionExamCreatedById(subQuestionId, userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        subQuestionRepository.deleteById(subQuestionId);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    void validateQuestionRequest(QuestionRequest request) {
        QuestionType type = request.getQuestionType();

        if (type == QuestionType.MULTIPLE_CHOICE || type == QuestionType.TRUE_FALSE) {
            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            long correctCount = request.getOptions().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .count();
            if (correctCount != 1) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            if (type == QuestionType.TRUE_FALSE && request.getOptions().size() != 2) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
        }

        if (type == QuestionType.SHORT_ANSWER) {
            // subQuestions can be empty at creation and added later via addSubQuestion
            // but if provided, validate each one
            if (request.getSubQuestions() != null) {
                request.getSubQuestions().forEach(sq -> {
                    if (sq.getOptions() == null || sq.getOptions().isEmpty()) {
                        throw new AppException(ErrorCode.INVALID_INPUT);
                    }
                    long correctCount = sq.getOptions().stream()
                            .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                            .count();
                    if (correctCount != 1) {
                        throw new AppException(ErrorCode.INVALID_INPUT);
                    }
                });
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    Exam findExamOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
    }

    Exam findAndVerifyOwnership(Long id, Long userId) {
        Exam exam = findExamOrThrow(id);
        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return exam;
    }

    void attachOptionsOrSubQuestions(Question question, QuestionRequest request) {
        QuestionType type = request.getQuestionType();

        if (type == QuestionType.MULTIPLE_CHOICE || type == QuestionType.TRUE_FALSE) {
            if (request.getOptions() != null) {
                request.getOptions().forEach(optReq -> {
                    QuestionOption opt = examMapper.toQuestionOption(optReq);
                    opt.setQuestion(question);
                    question.getOptions().add(opt);
                });
            }
        }

        if (type == QuestionType.SHORT_ANSWER) {
            if (request.getSubQuestions() != null) {
                request.getSubQuestions().forEach(sqReq -> {
                    SubQuestion sq = buildSubQuestion(sqReq, question);
                    question.getSubQuestions().add(sq);
                });
            }
        }
    }

    SubQuestion buildSubQuestion(SubQuestionRequest request, Question question) {
        SubQuestion sq = examMapper.toSubQuestion(request);
        sq.setQuestion(question);
        attachSubQuestionOptions(sq, request);
        return sq;
    }

    void attachSubQuestionOptions(SubQuestion subQuestion, SubQuestionRequest request) {
        if (request.getOptions() != null) {
            request.getOptions().forEach(optReq -> {
                SubQuestionOption opt = examMapper.toSubQuestionOption(optReq);
                opt.setSubQuestion(subQuestion);
                subQuestion.getOptions().add(opt);
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
        QuestionType type = question.getQuestionType();

        if (type == QuestionType.MULTIPLE_CHOICE || type == QuestionType.TRUE_FALSE) {
            qr.setOptions(examMapper.toQuestionOptionResponseList(question.getOptions()));
            qr.setSubQuestions(null);
        } else if (type == QuestionType.SHORT_ANSWER) {
            qr.setOptions(null);
            qr.setSubQuestions(
                question.getSubQuestions().stream()
                    .map(this::toSubQuestionResponse)
                    .collect(Collectors.toList())
            );
        }
        return qr;
    }

    SubQuestionResponse toSubQuestionResponse(SubQuestion sq) {
        SubQuestionResponse response = examMapper.toSubQuestionResponse(sq);
        response.setOptions(examMapper.toSubQuestionOptionResponseList(sq.getOptions()));
        return response;
    }
}