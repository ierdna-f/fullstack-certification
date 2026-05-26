package com.certified.fullstack.quiz.service.impl;

import com.certified.fullstack.exception.NotFoundException;
import com.certified.fullstack.notification.NotificationStatus;
import com.certified.fullstack.notification.service.NotificationService;
import com.certified.fullstack.quiz.dto.request.SubmitAttemptRequest;
import com.certified.fullstack.quiz.dto.response.AttemptResponse;
import com.certified.fullstack.quiz.dto.response.QuestionResponse;
import com.certified.fullstack.quiz.dto.response.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.response.SubmitAttemptResponse;
import com.certified.fullstack.quiz.entity.Attempt;
import com.certified.fullstack.quiz.entity.AttemptAnswer;
import com.certified.fullstack.quiz.entity.Question;
import com.certified.fullstack.quiz.entity.Quiz;
import com.certified.fullstack.quiz.repository.AttemptRepository;
import com.certified.fullstack.quiz.repository.QuizRepository;
import com.certified.fullstack.quiz.service.AttemptService;
import com.certified.fullstack.user.dto.AttemptDetailsResponse;
import com.certified.fullstack.user.dto.UserStatisticsResponse;
import com.certified.fullstack.user.entity.User;
import com.certified.fullstack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public AttemptResponse startAttempt(Long quizId, Long userId) {

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new NotFoundException("Quiz not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        boolean exists = attemptRepository.existsByQuizIdAndUserIdAndCompletedAtIsNull(quizId,userId);

        if (exists) {
            throw new RuntimeException("You already have an ongoing attempt");
        }

        Attempt attempt = new Attempt();

        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setCreatedAt(LocalDateTime.now());

        Attempt savedAttempt = attemptRepository.save(attempt);
        return new AttemptResponse(
                savedAttempt.getId(),
                quiz.getId(),
                user.getId(),
                new QuizDetailsResponse(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getDescription(),
                        quiz.getQuestions()
                                .stream()
                                .map(q -> new QuestionResponse(
                                        q.getId(),
                                        q.getText(),
                                        q.getOptions()
                                ))
                                .toList()
                )
        );
    }

    @Override
    public SubmitAttemptResponse submitAttempt(Long attemptId, SubmitAttemptRequest request) {

        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("Attempt not found"));

        if (attempt.getCompletedAt() != null) {
            throw new RuntimeException("Attempt already completed");
        }

        int totalQuestions = attempt.getQuiz().getQuestions().size();
        int correctAnswers = 0;

        for (SubmitAttemptRequest.Answer answer : request.getAnswers()) {

            Question question = attempt.getQuiz()
                    .getQuestions()
                    .stream()
                    .filter(q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst()
                    .orElseThrow();

            if (question.getCorrect().equals(answer.getAnswer())) {
                correctAnswers++;
            }
        }

        double score = ((double) correctAnswers / totalQuestions) * 100;

        String feedback;

        if (score >= 80) {
            feedback = "Excellent work!";
        } else if (score >= 60) {
            feedback = "Good effort, keep improving!";
        } else {
            feedback = "Keep practicing and try again!";
        }

        List<AttemptAnswer> answers = request.getAnswers()
                .stream()
                .map(a -> {

                    Question question = attempt.getQuiz()
                            .getQuestions()
                            .stream()
                            .filter(q -> q.getId().equals(a.getQuestionId()))
                            .findFirst()
                            .orElseThrow();

                    AttemptAnswer attemptAnswer = new AttemptAnswer();

                    attemptAnswer.setAttempt(attempt);
                    attemptAnswer.setQuestion(question);
                    attemptAnswer.setAnswer(a.getAnswer());

                    return attemptAnswer;
                })
                .collect(Collectors.toList());

        attempt.setNotificationStatus(NotificationStatus.PENDING);
        attempt.setAnswers(answers);
        attempt.setCompletedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        notificationService.sendQuizResultEmail(
                attempt.getId(),
                attempt.getUser().getName(),
                attempt.getUser().getEmail(),
                score
        );
        return new SubmitAttemptResponse(
            score,
            correctAnswers,
            totalQuestions,
            feedback
        );
    }

    @Override
    public List<AttemptResponse> getUserAttempts(Long userId) {

        return attemptRepository.findAllByUserId(userId)
                .stream()
                .map(attempt -> new AttemptResponse(
                        attempt.getId(),
                        attempt.getQuiz().getId(),
                        attempt.getUser().getId(),
                        null
                ))
                .toList();
    }

    @Override
    public AttemptDetailsResponse getAttemptDetails(Long attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new NotFoundException("Attempt not found"));

        int totalQuestions = attempt.getQuiz().getQuestions().size();

        int correctAnswers = attempt.getAnswers()
                .stream()
                .mapToInt(answer ->
                        answer.getAnswer().equals(answer.getQuestion().getCorrect()) ? 1 : 0
                )
                .sum();

        double score = ((double) correctAnswers / totalQuestions) * 100;

        return new AttemptDetailsResponse(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getQuiz().getId(),
                attempt.getQuiz().getTitle(),
                attempt.getCompletedAt(),
                score,
                attempt.getAnswers()
                        .stream()
                        .map(answer -> new AttemptDetailsResponse.QuestionResult(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getText(),
                                answer.getQuestion().getCorrect(),
                                answer.getAnswer(),
                                answer.getAnswer().equals(answer.getQuestion().getCorrect())
                        ))
                        .toList()
        );
    }

    @Override
    public UserStatisticsResponse getUserStatistics(Long userId) {

        List<Attempt> attempts = attemptRepository.findAllByUserId(userId);

        double averageScore = attempts.stream()
                .mapToDouble(attempt -> {

                    int totalQuestions = attempt.getQuiz().getQuestions().size();

                    int correctAnswers = attempt.getAnswers()
                            .stream()
                            .mapToInt(answer ->
                                    answer.getAnswer().equals(answer.getQuestion().getCorrect()) ? 1 : 0
                            )
                            .sum();

                    return ((double) correctAnswers / totalQuestions) * 100;
                })
                .average()
                .orElse(0);

        return new UserStatisticsResponse(
                userId,
                attempts.size(),
                averageScore
        );
    }
}