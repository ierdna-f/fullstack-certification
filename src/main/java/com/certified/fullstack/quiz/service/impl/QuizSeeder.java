package com.certified.fullstack.quiz.service.impl;

import com.certified.fullstack.quiz.dto.request.QuizRequest;
import com.certified.fullstack.quiz.service.QuizService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizSeeder {

    private final QuizService quizService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void seed() throws Exception {
        if (!quizService.getAllQuizzes().isEmpty()) {
            return;
        }
        List<QuizRequest> quizzes = objectMapper.readValue(new ClassPathResource("preload-quizzes.json").getInputStream(), new TypeReference<>() {});
        quizzes.forEach(quizService::createQuiz);
    }
}