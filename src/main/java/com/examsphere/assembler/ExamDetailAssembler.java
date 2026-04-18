package com.examsphere.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.PassageResponse;
import com.examsphere.dto.response.QuestionResponse;
import com.examsphere.enums.QuestionType;
import com.examsphere.mapper.ExamMapper;
import com.examsphere.model.Exam;
import com.examsphere.model.Passage;
import com.examsphere.model.Question;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamDetailAssembler {

    ExamMapper examMapper;

    public ExamDetailResponse toTeacherDetailResponse(
            Exam exam,
            List<Question> standaloneQuestions,
            List<Passage> passages,
            Map<Long, List<Question>> questionsByPassageId) {
        return buildDetailResponse(exam, standaloneQuestions, passages, questionsByPassageId, false);
    }

    public ExamDetailResponse toStudentDetailResponse(
            Exam exam,
            List<Question> standaloneQuestions,
            List<Passage> passages,
            Map<Long, List<Question>> questionsByPassageId) {
        return buildDetailResponse(exam, standaloneQuestions, passages, questionsByPassageId, true);
    }

    public ExamDetailResponse toTeacherDetailResponse(Exam exam) {
        ExamDetailResponse response = examMapper.toDetailResponse(exam);
        response.setQuestions(
                exam.getQuestions().stream()
                        .map(this::toTeacherQuestionResponse)
                        .toList()
        );
        response.setPassages(
                exam.getPassages().stream()
                        .map(this::toTeacherPassageResponse)
                        .toList()
        );
        return response;
    }

    public PassageResponse toTeacherPassageResponse(Passage passage) {
        return toPassageResponse(passage, passage.getQuestions(), false);
    }

    public QuestionResponse toTeacherQuestionResponse(Question question) {
        return toQuestionResponse(question, false);
    }

    ExamDetailResponse buildDetailResponse(
            Exam exam,
            List<Question> standaloneQuestions,
            List<Passage> passages,
            Map<Long, List<Question>> questionsByPassageId,
            boolean hideAnswers) {
        ExamDetailResponse response = examMapper.toDetailResponse(exam);
        response.setQuestions(
                standaloneQuestions.stream()
                        .map(question -> toQuestionResponse(question, hideAnswers))
                        .toList()
        );
        response.setPassages(
                passages.stream()
                        .map(passage -> toPassageResponse(
                                passage,
                                questionsByPassageId.getOrDefault(passage.getId(), Collections.emptyList()),
                                hideAnswers))
                        .toList()
        );
        return response;
    }

    PassageResponse toPassageResponse(Passage passage, List<Question> questions, boolean hideAnswers) {
        PassageResponse response = examMapper.toPassageResponse(passage);
        response.setQuestions(
                questions.stream()
                        .map(question -> toQuestionResponse(question, hideAnswers))
                        .toList()
        );
        return response;
    }

    QuestionResponse toQuestionResponse(Question question, boolean hideAnswers) {
        QuestionResponse response = examMapper.toQuestionResponse(question);

        boolean hasOptions =
                question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                question.getQuestionType() == QuestionType.TRUE_FALSE;

        if (!hasOptions) {
            response.setOptions(null);
            return response;
        }

        response.setOptions(
                examMapper.toQuestionOptionResponseList(new ArrayList<>(question.getOptions()))
        );

        if (hideAnswers && response.getOptions() != null) {
            response.getOptions().forEach(option -> option.setIsCorrect(null));
        }

        return response;
    }
}
