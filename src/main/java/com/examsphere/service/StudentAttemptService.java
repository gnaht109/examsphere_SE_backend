package com.examsphere.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.dto.request.AttemptAnswerRequest;
import com.examsphere.dto.response.AttemptAnswerResponse;
import com.examsphere.dto.response.AttemptQuestionOptionResultResponse;
import com.examsphere.dto.response.AttemptQuestionResultResponse;
import com.examsphere.dto.response.AttemptResponse;
import com.examsphere.dto.response.AttemptResultResponse;
import com.examsphere.enums.AttemptStatus;
import com.examsphere.enums.ExamStatus;
import com.examsphere.exception.AppException;
import com.examsphere.exception.ErrorCode;
import com.examsphere.model.Attempt;
import com.examsphere.model.AttemptAnswer;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.QuestionOption;
import com.examsphere.model.User;
import com.examsphere.repository.AttemptAnswerRepository;
import com.examsphere.repository.AttemptRepository;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StudentAttemptService {

    AttemptRepository attemptRepository;
    AttemptAnswerRepository attemptAnswerRepository;
    ExamRepository examRepository;
    QuestionRepository questionRepository;
    AuthService authService;

    @Transactional
    public AttemptResponse startAttempt(Long examId) {
        User student = authService.getCurrentUser();
        Exam exam = examRepository.findByIdBasicAndStatus(examId, ExamStatus.PUBLISHED)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_PUBLISHED));

        List<Attempt> existingAttempts = retainLatestAttempt(examId, student.getId());

        if (existingAttempts.isEmpty()) {
            return createNewAttempt(exam, student);
        }

        existingAttempts.forEach(this::finalizeIfExpired);

        List<Attempt> inProgressAttempts = existingAttempts.stream()
                .filter(attempt -> attempt.getStatus() == AttemptStatus.IN_PROGRESS)
                .toList();

        if (!inProgressAttempts.isEmpty()) {
            Attempt activeAttempt = inProgressAttempts.get(0);

            // Keep only the newest active attempt when historical duplicates already exist.
            inProgressAttempts.stream()
                    .skip(1)
                    .forEach(duplicateAttempt -> finalizeAttempt(duplicateAttempt, AttemptStatus.EXPIRED,
                            LocalDateTime.now()));

            return toAttemptResponse(activeAttempt);
        }

        Attempt latestAttempt = existingAttempts.stream()
                .max(Comparator
                        .comparing(Attempt::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Attempt::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new AppException(ErrorCode.ATTEMPT_NOT_FOUND));

        return resetAttempt(latestAttempt, exam);
    }

    @Transactional
    public AttemptResponse getAttempt(Long attemptId) {
        Long studentId = authService.getCurrentUserId();
        Attempt attempt = findOwnedAttempt(attemptId, studentId);
        finalizeIfExpired(attempt);
        return toAttemptResponse(attempt);
    }

    @Transactional
    public List<AttemptResponse> getAttempts(AttemptStatus status) {
        Long studentId = authService.getCurrentUserId();
        List<Attempt> attempts = retainLatestAttempts(studentId);

        return attempts.stream()
                .filter(attempt -> status == null || attempt.getStatus() == status)
                .map(this::toAttemptResponse)
                .toList();
    }

    @Transactional
    public AttemptAnswerResponse saveAnswer(Long attemptId, AttemptAnswerRequest request) {
        Long studentId = authService.getCurrentUserId();
        Attempt attempt = findOwnedAttempt(attemptId, studentId);
        finalizeIfExpired(attempt);
        ensureAttemptInProgress(attempt);

        Question question = questionRepository.findByIdAndExamIdWithOptions(request.getQuestionId(), attempt.getExam().getId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionOption selectedOption = question.getOptions().stream()
                .filter(option -> option.getId().equals(request.getSelectedOptionId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND));

        AttemptAnswer answer = attemptAnswerRepository.findByAttemptIdAndQuestionId(attemptId, question.getId())
                .orElseGet(() -> AttemptAnswer.builder()
                        .attempt(attempt)
                        .question(question)
                        .build());

        answer.setSelectedOption(selectedOption);
        answer.setAnsweredAt(LocalDateTime.now());
        answer.setIsCorrect(null);
        answer.setEarnedPoints(null);

        AttemptAnswer savedAnswer = attemptAnswerRepository.save(answer);
        return toAttemptAnswerResponse(savedAnswer);
    }

    @Transactional
    public AttemptResponse submitAttempt(Long attemptId) {
        Long studentId = authService.getCurrentUserId();
        Attempt attempt = findOwnedAttempt(attemptId, studentId);
        finalizeIfExpired(attempt);

        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            finalizeAttempt(attempt, AttemptStatus.SUBMITTED, LocalDateTime.now());
        }

        return toAttemptResponse(attempt);
    }

    @Transactional
    public AttemptResultResponse getResult(Long attemptId) {
        Long studentId = authService.getCurrentUserId();
        Attempt attempt = findOwnedAttempt(attemptId, studentId);
        finalizeIfExpired(attempt);

        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.ATTEMPT_NOT_FINISHED);
        }

        if (attempt.getScore() == null) {
            attempt.setScore(gradeAttempt(attempt));
            attemptRepository.save(attempt);
        }

        return toAttemptResultResponse(attempt);
    }

    Attempt createAttemptEntity(Exam exam, User student) {
        LocalDateTime now = LocalDateTime.now();
        return Attempt.builder()
                .exam(exam)
                .student(student)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(now)
                .expiresAt(now.plusMinutes(exam.getDuration()))
                .build();
    }

    AttemptResponse createNewAttempt(Exam exam, User student) {
        Attempt savedAttempt = attemptRepository.save(createAttemptEntity(exam, student));
        return toAttemptResponse(savedAttempt);
    }

    AttemptResponse resetAttempt(Attempt attempt, Exam exam) {
        attemptAnswerRepository.deleteByAttemptId(attempt.getId());

        LocalDateTime now = LocalDateTime.now();
        attempt.setExam(exam);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(now);
        attempt.setSubmittedAt(null);
        attempt.setExpiresAt(now.plusMinutes(exam.getDuration()));
        attempt.setScore(null);

        Attempt savedAttempt = attemptRepository.save(attempt);
        return toAttemptResponse(savedAttempt);
    }

    Attempt findOwnedAttempt(Long attemptId, Long studentId) {
        retainLatestAttempts(studentId);
        return attemptRepository.findByIdAndStudentId(attemptId, studentId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTEMPT_NOT_FOUND));
    }

    List<Attempt> retainLatestAttempt(Long examId, Long studentId) {
        List<Attempt> attempts = attemptRepository.findAllByExamIdAndStudentIdOrderByStartedAtDescIdDesc(examId, studentId);
        return retainLatestPerExam(attempts);
    }

    List<Attempt> retainLatestAttempts(Long studentId) {
        List<Attempt> attempts = attemptRepository.findByStudentIdOrderByStartedAtDescIdDesc(studentId);
        return retainLatestPerExam(attempts);
    }

    List<Attempt> retainLatestPerExam(List<Attempt> attempts) {
        if (attempts.isEmpty()) {
            return attempts;
        }

        Map<Long, List<Attempt>> attemptsByExamId = attempts.stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getExam().getId()));

        List<Long> obsoleteAttemptIds = attemptsByExamId.values().stream()
                .flatMap(group -> group.stream().skip(1))
                .map(Attempt::getId)
                .toList();

        if (!obsoleteAttemptIds.isEmpty()) {
            attemptAnswerRepository.deleteByAttemptIdIn(obsoleteAttemptIds);
            attemptRepository.deleteAllById(obsoleteAttemptIds);
        }

        return attemptsByExamId.values().stream()
                .map(group -> group.get(0))
                .peek(this::finalizeIfExpired)
                .sorted(Comparator
                        .comparing(Attempt::getStartedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Attempt::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    void finalizeIfExpired(Attempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (attempt.getExpiresAt() != null && !attempt.getExpiresAt().isAfter(now)) {
            finalizeAttempt(attempt, AttemptStatus.EXPIRED, now);
        }
    }

    void finalizeAttempt(Attempt attempt, AttemptStatus finalStatus, LocalDateTime finalizedAt) {
        attempt.setStatus(finalStatus);
        attempt.setSubmittedAt(finalizedAt);
        attempt.setScore(gradeAttempt(attempt));
        attemptRepository.save(attempt);
    }

    void ensureAttemptInProgress(Attempt attempt) {
        if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
            throw new AppException(ErrorCode.SUBMISSION_ALREADY_SUBMITTED);
        }
        if (attempt.getStatus() == AttemptStatus.EXPIRED) {
            throw new AppException(ErrorCode.SUBMISSION_EXPIRED);
        }
    }

    double gradeAttempt(Attempt attempt) {
        List<Question> questions = questionRepository.findQuestionsWithOptions(attempt.getExam().getId());
        Map<Long, AttemptAnswer> answersByQuestionId = attemptAnswerRepository.findByAttemptId(attempt.getId()).stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), Function.identity()));

        double score = 0.0;
        for (Question question : questions) {
            AttemptAnswer answer = answersByQuestionId.get(question.getId());
            if (answer == null) {
                continue;
            }

            QuestionOption correctOption = getCorrectOption(question);
            boolean isCorrect = correctOption != null
                    && answer.getSelectedOption() != null
                    && correctOption.getId().equals(answer.getSelectedOption().getId());

            double earnedPoints = isCorrect ? defaultPoints(question.getPoints()) : 0.0;
            answer.setIsCorrect(isCorrect);
            answer.setEarnedPoints(earnedPoints);
            score += earnedPoints;
        }

        attemptAnswerRepository.saveAll(answersByQuestionId.values());
        return roundScore(score);
    }

    QuestionOption getCorrectOption(Question question) {
        return question.getOptions().stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .findFirst()
                .orElse(null);
    }

    double defaultPoints(Double points) {
        return points != null ? points : 0.0;
    }

    double roundScore(double score) {
        return Math.round(score * 100.0) / 100.0;
    }

    AttemptResponse toAttemptResponse(Attempt attempt) {
        LocalDateTime now = LocalDateTime.now();
        long remainingSeconds = attempt.getStatus() == AttemptStatus.IN_PROGRESS && attempt.getExpiresAt() != null
                ? Math.max(0L, Duration.between(now, attempt.getExpiresAt()).getSeconds())
                : 0L;

        return AttemptResponse.builder()
                .id(attempt.getId())
                .examId(attempt.getExam().getId())
                .examTitle(attempt.getExam().getTitle())
                .studentId(attempt.getStudent().getId())
                .status(attempt.getStatus())
                .durationMinutes(attempt.getExam().getDuration())
                .totalScore(attempt.getExam().getTotalScore())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .expiresAt(attempt.getExpiresAt())
                .remainingSeconds(remainingSeconds)
                .totalQuestions(Math.toIntExact(questionRepository.countByExamId(attempt.getExam().getId())))
                .answeredQuestions(Math.toIntExact(attemptAnswerRepository.countByAttemptId(attempt.getId())))
                .score(attempt.getScore())
                .build();
    }

    AttemptAnswerResponse toAttemptAnswerResponse(AttemptAnswer answer) {
        return AttemptAnswerResponse.builder()
                .attemptId(answer.getAttempt().getId())
                .questionId(answer.getQuestion().getId())
                .selectedOptionId(answer.getSelectedOption().getId())
                .answeredAt(answer.getAnsweredAt())
                .build();
    }

    AttemptResultResponse toAttemptResultResponse(Attempt attempt) {
        List<Question> questions = questionRepository.findQuestionsWithOptions(attempt.getExam().getId());
        Map<Long, AttemptAnswer> answersByQuestionId = attemptAnswerRepository.findDetailedByAttemptId(attempt.getId()).stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), Function.identity()));

        List<AttemptQuestionResultResponse> questionResults = questions.stream()
                .map(question -> toQuestionResultResponse(question, answersByQuestionId.get(question.getId())))
                .toList();

        int totalQuestions = questionResults.size();
        int answeredQuestions = (int) questionResults.stream().filter(AttemptQuestionResultResponse::getAnswered).count();
        int correctAnswers = (int) questionResults.stream().filter(AttemptQuestionResultResponse::getCorrect).count();
        int unansweredQuestions = totalQuestions - answeredQuestions;

        return AttemptResultResponse.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExam().getId())
                .examTitle(attempt.getExam().getTitle())
                .studentId(attempt.getStudent().getId())
                .status(attempt.getStatus())
                .totalScore(attempt.getExam().getTotalScore())
                .score(attempt.getScore())
                .totalQuestions(totalQuestions)
                .answeredQuestions(answeredQuestions)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(answeredQuestions - correctAnswers)
                .unansweredQuestions(unansweredQuestions)
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .questions(questionResults)
                .build();
    }

    AttemptQuestionResultResponse toQuestionResultResponse(Question question, AttemptAnswer answer) {
        QuestionOption correctOption = getCorrectOption(question);
        boolean answered = answer != null && answer.getSelectedOption() != null;
        boolean correct = answered && Boolean.TRUE.equals(answer.getIsCorrect());
        List<AttemptQuestionOptionResultResponse> optionResults = question.getOptions().stream()
                .sorted(Comparator
                        .comparing(QuestionOption::getOptionOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(QuestionOption::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(option -> AttemptQuestionOptionResultResponse.builder()
                        .optionId(option.getId())
                        .content(option.getContent())
                        .optionOrder(option.getOptionOrder())
                        .correct(Boolean.TRUE.equals(option.getIsCorrect()))
                        .selected(answered && option.getId().equals(answer.getSelectedOption().getId()))
                        .build())
                .toList();

        return AttemptQuestionResultResponse.builder()
                .questionId(question.getId())
                .questionOrder(question.getQuestionOrder())
                .content(question.getContent())
                .questionType(question.getQuestionType() != null ? question.getQuestionType().name() : null)
                .passageId(question.getPassage() != null ? question.getPassage().getId() : null)
                .passageContent(question.getPassage() != null ? question.getPassage().getContent() : null)
                .points(question.getPoints())
                .earnedPoints(answer != null ? answer.getEarnedPoints() : 0.0)
                .answered(answered)
                .correct(correct)
                .selectedOptionId(answered ? answer.getSelectedOption().getId() : null)
                .selectedOptionContent(answered ? answer.getSelectedOption().getContent() : null)
                .correctOptionId(correctOption != null ? correctOption.getId() : null)
                .correctOptionContent(correctOption != null ? correctOption.getContent() : null)
                .options(optionResults)
                .build();
    }
}
