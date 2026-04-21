package com.examsphere.service;

import com.examsphere.repository.AnswerRepository;
import com.examsphere.repository.QuestionRepository;
import com.examsphere.repository.SessionRepository;
import com.examsphere.dto.request.AnswerRequest;
import com.examsphere.model.Answer;
import com.examsphere.model.Question;
import com.examsphere.model.Session;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AnswerService {

}