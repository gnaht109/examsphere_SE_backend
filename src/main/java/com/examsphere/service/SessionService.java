package com.examsphere.service;

import com.examsphere.repository.AnswerRepository;
import com.examsphere.repository.ExamRepository;
import com.examsphere.repository.QuestionRepository;
import com.examsphere.repository.SessionRepository;

import jakarta.transaction.Transactional;

import com.examsphere.mapper.SessionMapper;
import com.examsphere.dto.request.AnswerRequest;
import com.examsphere.dto.response.SessionDetailResponse;
import com.examsphere.dto.response.SessionResponse;
import com.examsphere.enums.ExamStatus;
import com.examsphere.enums.SessionStatus;
import com.examsphere.mapper.QuestionMapper;
import com.examsphere.model.Answer;
import com.examsphere.model.Exam;
import com.examsphere.model.Question;
import com.examsphere.model.Session;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {

    private SessionRepository sessionRepository;
    private ExamRepository examRepository;
    private SessionMapper sessionMapper;
    private AuthService authService;
    private QuestionRepository questionRepository;
    private QuestionMapper questionMapper;
    private AnswerRepository answerRepository;
    
    public SessionResponse getBySessionId(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return sessionMapper.toResponse(session);
    }

    public List<SessionResponse> getMy() {
      Long userId = authService.getCurrentUserId();       
       return sessionRepository.findByStudentId(userId)
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }  

    public SessionDetailResponse getSessionDetail(Long sessionId) {
 
       Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found")) ; 

       Long examId = session.getExam().getId();
 
       List<Question> questions = questionRepository.findByExamIdWithOptions(session.getExam().getId()); 
       return SessionDetailResponse.builder()
                .sessionId(session.getId())
                .examId(examId)
                .durationMinutes(session.getDurationMinutes())
                .startedAt(session.getStartedAt())
                .questions(
                      questions.stream()
                               .map(questionMapper::toQuestionResponse)
                               .toList()
                )
                .build();
       }
       
    public SessionDetailResponse startSession(Long examId) {

        // get current user id
        Long userId = authService.getCurrentUserId();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Session session = new Session();
        session.setExam(exam);
        session.setStudentId(userId);
        session.setDurationMinutes(exam.getDuration());
        session.setStartedAt(LocalDateTime.now());

        session = sessionRepository.save(session);

        List<Question> questions =
                questionRepository.findByExamIdWithOptions(examId);

        return sessionMapper.toDetailResponse(session, questions);
    }       

   public void saveAnswer(Long sessionId, AnswerRequest request) {

      Session session = sessionRepository.findById(sessionId)
               .orElseThrow();

      Answer answer = answerRepository
               .findBySessionIdAndQuestionOrder(
                     sessionId,
                     request.getQuestionOrder()
               )
               .orElse(null);

      if (answer == null) {
         answer = Answer.builder()
                  .session(session)
                  .questionOrder(request.getQuestionOrder())
                  .build();
      }

      answer.setAnswer(request.getAnswer());

      answerRepository.save(answer);
   }

    @Transactional
    public void submitSession(Long sessionId) {
 
       Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found")); 
 
       session.setSubmittedAt(LocalDateTime.now());
       session.setStatus(SessionStatus.SUBMITTED); 
 
       sessionRepository.save(session);
    }
}