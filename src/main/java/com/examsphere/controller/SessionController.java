package com.examsphere.controller;

import com.examsphere.dto.request.AnswerRequest;
import com.examsphere.dto.response.ApiResponse;
import com.examsphere.dto.response.ExamDetailResponse;
import com.examsphere.dto.response.ExamResponse;
import com.examsphere.dto.response.SessionDetailResponse;
import com.examsphere.dto.response.SessionResponse;
import com.examsphere.model.Session;
import com.examsphere.service.SessionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

   //  @GetMapping("/sessions/my")
   //  public List<SessionResponse> getMySessions() {
   //      return sessionService.getMySessions();
   //  }

   //  @GetMapping("/sessions/{sessionId}")
   //  public SessionResponse getBySessionId(@PathVariable Long sessionId) {
   //      return sessionService.getBySessionId(sessionId);
   //  }

    @GetMapping("/sessions/my")
    public List<SessionResponse> getMy() {
      return sessionService.getMy();
    }    
    
   //  @GetMapping("/sessions/my")
   //  ApiResponse<List<ExamResponse>> getMyExams() {
   //      return ApiResponse.<List<ExamResponse>>builder()
   //              .data(teacherExamService.getMyExams())
   //              .build();
   //  }

    @GetMapping("/sessions/{sessionId}")
    public SessionDetailResponse getSession(@PathVariable Long sessionId) {
      return sessionService.getSessionDetail(sessionId);
    }

    @PostMapping("/sessions/start/{examId}")
    public SessionDetailResponse startSession(@PathVariable Long examId) {
        return sessionService.startSession(examId);
    }    

    @PutMapping("/sessions/{sessionId}/answer")
    public ResponseEntity<?> saveAnswer(
        @PathVariable Long sessionId,
        @RequestBody AnswerRequest request
    ) {
       sessionService.saveAnswer(sessionId, request);
       return ResponseEntity.ok().build();
    }    

    @PostMapping("/sessions/{sessionId}/submit")
    public ResponseEntity<Void> submitSession(@PathVariable Long sessionId) {
       sessionService.submitSession(sessionId);
       return ResponseEntity.ok().build();
    }    
}