package com.certified.fullstack.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSummaryResponse {

    private Long id;
    private String title;
    private String description;
}