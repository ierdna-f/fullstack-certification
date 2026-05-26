package com.certified.fullstack.quiz.service.impl;

import com.certified.fullstack.exception.NotFoundException;
import com.certified.fullstack.notification.service.NotificationService;
import com.certified.fullstack.quiz.dto.request.SubmitAttemptRequest;
import com.certified.fullstack.quiz.dto.response.AttemptResponse;
import com.certified.fullstack.quiz.dto.response.SubmitAttemptResponse;
import com.certified.fullstack.quiz.entity.Attempt;
import com.certified.fullstack.quiz.entity.AttemptAnswer;
import com.certified.fullstack.quiz.entity.Question;
import com.certified.fullstack.quiz.entity.Quiz;
import com.certified.fullstack.quiz.repository.AttemptRepository;
import com.certified.fullstack.quiz.repository.QuizRepository;
import com.certified.fullstack.user.dto.AttemptDetailsResponse;
import com.certified.fullstack.user.dto.UserStatisticsResponse;
import com.certified.fullstack.user.entity.User;
import com.certified.fullstack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptServiceImplTest {

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AttemptServiceImpl attemptService;

    private Quiz quiz;
    private User user;
    private Attempt attempt;
    private Question question1;
    private Question question2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // Initialize User
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Initialize Questions
        question1 = new Question();
        question1.setId(1L);
        question1.setText("What is 2+2?");
        question1.setCorrect(0);
        question1.setOptions(List.of("4", "5", "6"));

        question2 = new Question();
        question2.setId(2L);
        question2.setText("What is 3+3?");
        question2.setCorrect(0);
        question2.setOptions(List.of("6", "7", "8"));

        // Initialize Quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Math Quiz");
        quiz.setDescription("Basic math questions");
        quiz.setQuestions(List.of(question1, question2));

        // Initialize Attempt
        attempt = new Attempt();
        attempt.setId(1L);
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setCreatedAt(now);
        attempt.setAnswers(new ArrayList<>());
    }

    // ============= startAttempt Tests =============

    @Test
    void testStartAttempt_Success() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(attemptRepository.existsByQuizIdAndUserIdAndCompletedAtIsNull(1L, 1L)).thenReturn(false);
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);

        // Act
        AttemptResponse response = attemptService.startAttempt(1L, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getAttemptId());
        assertEquals(1L, response.getQuizId());
        assertEquals(1L, response.getUserId());
        verify(attemptRepository, times(1)).save(any(Attempt.class));
    }

    @Test
    void testStartAttempt_QuizNotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> attemptService.startAttempt(1L, 1L));
        verify(quizRepository, times(1)).findById(1L);
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void testStartAttempt_UserNotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> attemptService.startAttempt(1L, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void testStartAttempt_OngoingAttemptExists() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(attemptRepository.existsByQuizIdAndUserIdAndCompletedAtIsNull(1L, 1L)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> attemptService.startAttempt(1L, 1L));
        assertEquals("You already have an ongoing attempt", exception.getMessage());
        verify(attemptRepository, never()).save(any());
    }

    // ============= submitAttempt Tests =============

    @Test
    void testSubmitAttempt_AllCorrect() {
        // Arrange
        List<SubmitAttemptRequest.Answer> answers = List.of(
                new SubmitAttemptRequest.Answer(1L, 0),
                new SubmitAttemptRequest.Answer(2L, 0)
        );
        SubmitAttemptRequest request = new SubmitAttemptRequest(answers);

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);

        // Act
        SubmitAttemptResponse response = attemptService.submitAttempt(1L, request);

        // Assert
        assertEquals(100.0, response.getScore());
        assertEquals(2, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
        assertEquals("Excellent work!", response.getFeedback());
        verify(notificationService, times(1)).sendQuizResultEmail(anyLong(), anyString(), anyString(), anyDouble());
        verify(attemptRepository, times(1)).save(any(Attempt.class));
    }

    @Test
    void testSubmitAttempt_PartiallyCorrect() {
        // Arrange
        List<SubmitAttemptRequest.Answer> answers = List.of(
                new SubmitAttemptRequest.Answer(1L, 0),
                new SubmitAttemptRequest.Answer(2L, 1)
        );
        SubmitAttemptRequest request = new SubmitAttemptRequest(answers);

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);

        // Act
        SubmitAttemptResponse response = attemptService.submitAttempt(1L, request);

        // Assert
        assertEquals(50.0, response.getScore());
        assertEquals(1, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
        assertEquals("Good effort, keep improving!", response.getFeedback());
    }

    @Test
    void testSubmitAttempt_AllIncorrect() {
        // Arrange
        List<SubmitAttemptRequest.Answer> answers = List.of(
                new SubmitAttemptRequest.Answer(1L, 1),
                new SubmitAttemptRequest.Answer(2L, 2)
        );
        SubmitAttemptRequest request = new SubmitAttemptRequest(answers);

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);

        // Act
        SubmitAttemptResponse response = attemptService.submitAttempt(1L, request);

        // Assert
        assertEquals(0.0, response.getScore());
        assertEquals(0, response.getCorrectAnswers());
        assertEquals(2, response.getTotalQuestions());
        assertEquals("Keep practicing and try again!", response.getFeedback());
    }

    @Test
    void testSubmitAttempt_ScoreBetween60And80() {
        // Arrange - Create 5 questions, 4 correct (80% would be excellent, so need less)
        Question q3 = new Question();
        q3.setId(3L);
        q3.setText("Q3");
        q3.setCorrect(0);

        Question q4 = new Question();
        q4.setId(4L);
        q4.setText("Q4");
        q4.setCorrect(0);

        Question q5 = new Question();
        q5.setId(5L);
        q5.setText("Q5");
        q5.setCorrect(0);

        quiz.setQuestions(List.of(question1, question2, q3, q4, q5));

        List<SubmitAttemptRequest.Answer> answers = List.of(
                new SubmitAttemptRequest.Answer(1L, 0),
                new SubmitAttemptRequest.Answer(2L, 0),
                new SubmitAttemptRequest.Answer(3L, 0),
                new SubmitAttemptRequest.Answer(4L, 1),
                new SubmitAttemptRequest.Answer(5L, 1)
        );
        SubmitAttemptRequest request = new SubmitAttemptRequest(answers);

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepository.save(any(Attempt.class))).thenReturn(attempt);

        // Act
        SubmitAttemptResponse response = attemptService.submitAttempt(1L, request);

        // Assert
        assertEquals(60.0, response.getScore());
        assertEquals(3, response.getCorrectAnswers());
        assertEquals("Good effort, keep improving!", response.getFeedback());
    }

    @Test
    void testSubmitAttempt_AttemptNotFound() {
        // Arrange
        SubmitAttemptRequest request = new SubmitAttemptRequest(List.of());
        when(attemptRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> attemptService.submitAttempt(1L, request));
    }

    @Test
    void testSubmitAttempt_AlreadyCompleted() {
        // Arrange
        attempt.setCompletedAt(now);
        SubmitAttemptRequest request = new SubmitAttemptRequest(List.of());

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> attemptService.submitAttempt(1L, request));
        assertEquals("Attempt already completed", exception.getMessage());
    }

    // ============= getUserAttempts Tests =============

    @Test
    void testGetUserAttempts_Success() {
        // Arrange
        Attempt attempt2 = new Attempt();
        attempt2.setId(2L);
        attempt2.setQuiz(quiz);
        attempt2.setUser(user);

        List<Attempt> attempts = List.of(attempt, attempt2);
        when(attemptRepository.findAllByUserId(1L)).thenReturn(attempts);

        // Act
        List<AttemptResponse> responses = attemptService.getUserAttempts(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getAttemptId());
        assertEquals(2L, responses.get(1).getAttemptId());
        verify(attemptRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void testGetUserAttempts_Empty() {
        // Arrange
        when(attemptRepository.findAllByUserId(1L)).thenReturn(List.of());

        // Act
        List<AttemptResponse> responses = attemptService.getUserAttempts(1L);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // ============= getAttemptDetails Tests =============

    @Test
    void testGetAttemptDetails_Success() {
        // Arrange
        AttemptAnswer answer1 = new AttemptAnswer();
        answer1.setQuestion(question1);
        answer1.setAnswer(0);

        AttemptAnswer answer2 = new AttemptAnswer();
        answer2.setQuestion(question2);
        answer2.setAnswer(1);

        attempt.setAnswers(List.of(answer1, answer2));
        attempt.setCompletedAt(now);

        when(attemptRepository.findById(1L)).thenReturn(Optional.of(attempt));

        // Act
        AttemptDetailsResponse response = attemptService.getAttemptDetails(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getAttemptId());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getQuizId());
        assertEquals("Math Quiz", response.getQuizTitle());
        assertEquals(now, response.getCompletedAt());
        assertEquals(50.0, response.getScore());
        assertEquals(2, response.getQuestions().size());

        // Verify question results
        AttemptDetailsResponse.QuestionResult q1 = response.getQuestions().get(0);
        assertEquals(1L, q1.getQuestionId());
        assertEquals("What is 2+2?", q1.getQuestion());
        assertEquals(0, q1.getCorrectAnswer());
        assertEquals(0, q1.getUserAnswer());
        assertTrue(q1.isCorrect());

        AttemptDetailsResponse.QuestionResult q2 = response.getQuestions().get(1);
        assertEquals(2L, q2.getQuestionId());
        assertEquals("What is 3+3?", q2.getQuestion());
        assertEquals(0, q2.getCorrectAnswer());
        assertEquals(1, q2.getUserAnswer());
        assertFalse(q2.isCorrect());
    }

    @Test
    void testGetAttemptDetails_AttemptNotFound() {
        // Arrange
        when(attemptRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> attemptService.getAttemptDetails(1L));
    }

    // ============= getUserStatistics Tests =============

    @Test
    void testGetUserStatistics_Success() {
        // Arrange
        AttemptAnswer answer1 = new AttemptAnswer();
        answer1.setQuestion(question1);
        answer1.setAnswer(0);

        AttemptAnswer answer2 = new AttemptAnswer();
        answer2.setQuestion(question2);
        answer2.setAnswer(0);

        attempt.setAnswers(List.of(answer1, answer2));

        Attempt attempt2 = new Attempt();
        attempt2.setId(2L);
        attempt2.setQuiz(quiz);
        attempt2.setUser(user);

        AttemptAnswer answer3 = new AttemptAnswer();
        answer3.setQuestion(question1);
        answer3.setAnswer(1);

        attempt2.setAnswers(List.of(answer3));

        List<Attempt> attempts = List.of(attempt, attempt2);
        when(attemptRepository.findAllByUserId(1L)).thenReturn(attempts);

        // Act
        UserStatisticsResponse response = attemptService.getUserStatistics(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(2, response.getTotalAttempts());
        assertEquals(75.0, response.getAverageScore()); // (100 + 50) / 2
    }

    @Test
    void testGetUserStatistics_NoAttempts() {
        // Arrange
        when(attemptRepository.findAllByUserId(1L)).thenReturn(List.of());

        // Act
        UserStatisticsResponse response = attemptService.getUserStatistics(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(0, response.getTotalAttempts());
        assertEquals(0.0, response.getAverageScore());
    }

    @Test
    void testGetUserStatistics_SingleAttempt() {
        // Arrange
        AttemptAnswer answer1 = new AttemptAnswer();
        answer1.setQuestion(question1);
        answer1.setAnswer(0);

        attempt.setAnswers(List.of(answer1));

        when(attemptRepository.findAllByUserId(1L)).thenReturn(List.of(attempt));

        // Act
        UserStatisticsResponse response = attemptService.getUserStatistics(1L);

        // Assert
        assertEquals(1L, response.getUserId());
        assertEquals(1, response.getTotalAttempts());
        assertEquals(50.0, response.getAverageScore()); // 1 correct out of 2
    }
}