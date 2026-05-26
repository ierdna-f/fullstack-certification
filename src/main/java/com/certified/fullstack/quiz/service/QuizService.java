package com.certified.fullstack.quiz.service;

import com.certified.fullstack.quiz.dto.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.QuizRequest;
import com.certified.fullstack.quiz.dto.QuizSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuizService {
    List<QuizSummaryResponse> getAllQuizzes();
    QuizDetailsResponse getQuizById(Long id);
    QuizDetailsResponse createQuiz(QuizRequest request);
}