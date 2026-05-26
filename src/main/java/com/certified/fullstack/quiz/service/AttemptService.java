package com.certified.fullstack.quiz.service;


import com.certified.fullstack.quiz.dto.request.SubmitAttemptRequest;
import com.certified.fullstack.quiz.dto.response.AttemptResponse;
import com.certified.fullstack.quiz.dto.response.SubmitAttemptResponse;
import com.certified.fullstack.user.dto.AttemptDetailsResponse;
import com.certified.fullstack.user.dto.UserStatisticsResponse;

import java.util.List;

public interface AttemptService {

    AttemptResponse startAttempt(Long quizId, Long userId);

    SubmitAttemptResponse submitAttempt(
            Long attemptId,
            SubmitAttemptRequest request
    );

    List<AttemptResponse> getUserAttempts(Long userId);

    AttemptDetailsResponse getAttemptDetails(Long attemptId);

    UserStatisticsResponse getUserStatistics(Long userId);
}