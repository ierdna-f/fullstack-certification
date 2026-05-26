package com.certified.fullstack.user.controller;

import com.certified.fullstack.quiz.dto.response.AttemptResponse;
import com.certified.fullstack.quiz.service.AttemptService;
import com.certified.fullstack.user.dto.AttemptDetailsResponse;
import com.certified.fullstack.user.dto.UserRequest;
import com.certified.fullstack.user.dto.UserResponse;
import com.certified.fullstack.user.dto.UserStatisticsResponse;
import com.certified.fullstack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AttemptService attemptService;

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/attempts")
    public List<AttemptResponse> getUserAttempts(@PathVariable Long userId) {
        return attemptService.getUserAttempts(userId);
    }

    @GetMapping("/{userId}/statistics")
    public UserStatisticsResponse getUserStatistics(@PathVariable Long userId) {
        return attemptService.getUserStatistics(userId);
    }

}