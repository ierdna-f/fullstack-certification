package com.certified.fullstack.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAttemptResponse {

    private double score;

    private int correctAnswers;

    private int totalQuestions;

    private String feedback;
}