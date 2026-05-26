package com.certified.fullstack.notification.service;

import com.certified.fullstack.notification.NotificationStatus;
import com.certified.fullstack.quiz.entity.Attempt;
import com.certified.fullstack.quiz.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AttemptRepository attemptRepository;

    @Async
    public void sendQuizResultEmail(Long attemptId, String name, String email, double score) {
        try {
            System.out.println("Sending mock email to " + name + " <" + email + ">");
            System.out.println("Quiz result score: " + score);

            Attempt attempt = attemptRepository.findById(attemptId).orElseThrow();
            attempt.setNotificationStatus(NotificationStatus.SENT);
            attemptRepository.save(attempt);

        } catch (Exception e) {
            Attempt attempt = attemptRepository.findById(attemptId).orElseThrow();
            attempt.setNotificationStatus(NotificationStatus.FAILED);
            attemptRepository.save(attempt);
        }
    }
}