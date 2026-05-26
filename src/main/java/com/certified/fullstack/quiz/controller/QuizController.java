package com.certified.fullstack.quiz.controller;

import com.certified.fullstack.quiz.dto.request.SubmitAttemptRequest;
import com.certified.fullstack.quiz.dto.response.AttemptResponse;
import com.certified.fullstack.quiz.dto.response.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.request.QuizRequest;
import com.certified.fullstack.quiz.dto.response.QuizSummaryResponse;
import com.certified.fullstack.quiz.dto.response.SubmitAttemptResponse;
import com.certified.fullstack.quiz.service.AttemptService;
import com.certified.fullstack.quiz.service.QuizService;
import com.certified.fullstack.user.dto.AttemptDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final AttemptService attemptService;

    @GetMapping
    public List<QuizSummaryResponse> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public QuizDetailsResponse getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizDetailsResponse createQuiz(@RequestBody QuizRequest request) {
        return quizService.createQuiz(request);
    }

    @PostMapping("/{quizId}/attempts/start")
    public AttemptResponse startAttempt(@PathVariable Long quizId, @RequestParam Long userId) {
        return attemptService.startAttempt(quizId, userId);
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public SubmitAttemptResponse submitAttempt(@PathVariable Long attemptId, @RequestBody SubmitAttemptRequest request) {
        return attemptService.submitAttempt(attemptId, request);
    }

    @GetMapping("/attempts/{attemptId}")
    public AttemptDetailsResponse getAttemptDetails(@PathVariable Long attemptId) {
        return attemptService.getAttemptDetails(attemptId);
    }


}