package com.certified.fullstack.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDetailsResponse {

    private Long id;
    private String title;
    private String description;
    private List<QuestionResponse> questions;
}