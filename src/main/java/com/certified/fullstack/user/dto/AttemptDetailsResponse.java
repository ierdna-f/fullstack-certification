package com.certified.fullstack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDetailsResponse {

    private Long attemptId;

    private Long userId;

    private Long quizId;

    private String quizTitle;

    private LocalDateTime completedAt;

    private double score;

    private List<QuestionResult> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {

        private Long questionId;

        private String question;

        private Integer correctAnswer;

        private Integer userAnswer;

        private boolean correct;
    }
}