package com.certified.fullstack.quiz.controller;

import com.certified.fullstack.quiz.dto.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.QuizRequest;
import com.certified.fullstack.quiz.dto.QuizSummaryResponse;
import com.certified.fullstack.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

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
}