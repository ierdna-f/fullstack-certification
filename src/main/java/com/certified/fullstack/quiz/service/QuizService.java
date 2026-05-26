package com.certified.fullstack.quiz.service;

import com.certified.fullstack.quiz.dto.response.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.request.QuizRequest;
import com.certified.fullstack.quiz.dto.response.QuizSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuizService {
    List<QuizSummaryResponse> getAllQuizzes();
    QuizDetailsResponse getQuizById(Long id);
    QuizDetailsResponse createQuiz(QuizRequest request);
}