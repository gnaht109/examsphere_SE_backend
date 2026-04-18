package com.examsphere.service;

import java.util.List;
import java.util.Map;

import com.examsphere.model.Passage;
import com.examsphere.model.Question;

public record ExamDetailQueryData(
        List<Question> standaloneQuestions,
        List<Passage> passages,
        Map<Long, List<Question>> questionsByPassageId) {
}
