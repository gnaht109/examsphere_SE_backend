package com.examsphere.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examsphere.model.Passage;
import com.examsphere.model.Question;
import com.examsphere.repository.PassageRepository;
import com.examsphere.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ExamDetailQueryService {

    PassageRepository passageRepository;
    QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public ExamDetailQueryData loadExamDetailData(Long examId) {
        List<Passage> passages = passageRepository.findPassages(examId);

        return new ExamDetailQueryData(
                questionRepository.findStandaloneQuestions(examId),
                passages,
                loadQuestionsByPassage(examId)
        );
    }

    Map<Long, List<Question>> loadQuestionsByPassage(Long examId) {
        return questionRepository.findPassageQuestionsByExamId(examId).stream()
                .collect(Collectors.toMap(
                        question -> question.getPassage().getId(),
                        List::of,
                        (left, right) -> {
                            java.util.ArrayList<Question> merged = new java.util.ArrayList<>(left);
                            merged.addAll(right);
                            return merged;
                        }
                ));
    }
}
