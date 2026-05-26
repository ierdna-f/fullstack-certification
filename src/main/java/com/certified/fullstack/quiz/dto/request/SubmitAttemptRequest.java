package com.certified.fullstack.quiz.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAttemptRequest {

    private List<Answer> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {

        private Long questionId;

        private Integer answer;
    }
}