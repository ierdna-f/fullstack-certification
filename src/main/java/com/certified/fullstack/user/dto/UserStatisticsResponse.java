package com.certified.fullstack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {

    private Long userId;

    private int totalAttempts;

    private double averageScore;
}