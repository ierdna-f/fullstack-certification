package com.certified.fullstack.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponse {

    private Long attemptId;

    private Long quizId;

    private Long userId;

    private QuizDetailsResponse quiz;
}