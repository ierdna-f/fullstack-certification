package com.certified.fullstack.quiz.service.impl;

import com.certified.fullstack.exception.NotFoundException;
import com.certified.fullstack.quiz.dto.request.QuestionRequest;
import com.certified.fullstack.quiz.dto.request.QuizRequest;
import com.certified.fullstack.quiz.dto.response.QuestionResponse;
import com.certified.fullstack.quiz.dto.response.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.response.QuizSummaryResponse;
import com.certified.fullstack.quiz.entity.Question;
import com.certified.fullstack.quiz.entity.Quiz;
import com.certified.fullstack.quiz.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Quiz quiz;
    private Question question1;
    private Question question2;

    @BeforeEach
    void setUp() {
        // Initialize questions
        question1 = new Question();
        question1.setId(1L);
        question1.setText("What is the capital of France?");
        question1.setOptions(List.of("Paris", "London", "Berlin"));
        question1.setCorrect(0);

        question2 = new Question();
        question2.setId(2L);
        question2.setText("What is 2+2?");
        question2.setOptions(List.of("3", "4", "5"));
        question2.setCorrect(1);

        // Initialize quiz
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("General Knowledge");
        quiz.setDescription("A quiz about general knowledge");
        quiz.setQuestions(List.of(question1, question2));

        // Set quiz reference for questions
        question1.setQuiz(quiz);
        question2.setQuiz(quiz);
    }

    // ============= getAllQuizzes Tests =============

    @Test
    void testGetAllQuizzes_Success() {
        // Arrange
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Biology Quiz");
        quiz2.setDescription("Questions about biology");
        quiz2.setQuestions(List.of());

        List<Quiz> quizzes = List.of(quiz, quiz2);
        when(quizRepository.findAll()).thenReturn(quizzes);

        // Act
        List<QuizSummaryResponse> responses = quizService.getAllQuizzes();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        QuizSummaryResponse response1 = responses.get(0);
        assertEquals(1L, response1.getId());
        assertEquals("General Knowledge", response1.getTitle());
        assertEquals("A quiz about general knowledge", response1.getDescription());

        QuizSummaryResponse response2 = responses.get(1);
        assertEquals(2L, response2.getId());
        assertEquals("Biology Quiz", response2.getTitle());
        assertEquals("Questions about biology", response2.getDescription());

        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void testGetAllQuizzes_Empty() {
        // Arrange
        when(quizRepository.findAll()).thenReturn(List.of());

        // Act
        List<QuizSummaryResponse> responses = quizService.getAllQuizzes();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void testGetAllQuizzes_SingleQuiz() {
        // Arrange
        List<Quiz> quizzes = List.of(quiz);
        when(quizRepository.findAll()).thenReturn(quizzes);

        // Act
        List<QuizSummaryResponse> responses = quizService.getAllQuizzes();

        // Assert
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("General Knowledge", responses.get(0).getTitle());
    }

    // ============= getQuizById Tests =============

    @Test
    void testGetQuizById_Success() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // Act
        QuizDetailsResponse response = quizService.getQuizById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("General Knowledge", response.getTitle());
        assertEquals("A quiz about general knowledge", response.getDescription());
        assertEquals(2, response.getQuestions().size());

        // Verify first question
        QuestionResponse question1Response = response.getQuestions().get(0);
        assertEquals(1L, question1Response.getId());
        assertEquals("What is the capital of France?", question1Response.getText());
        assertEquals(List.of("Paris", "London", "Berlin"), question1Response.getOptions());

        // Verify second question
        QuestionResponse question2Response = response.getQuestions().get(1);
        assertEquals(2L, question2Response.getId());
        assertEquals("What is 2+2?", question2Response.getText());
        assertEquals(List.of("3", "4", "5"), question2Response.getOptions());

        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void testGetQuizById_QuizNotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> quizService.getQuizById(1L));
        assertEquals("Quiz not found", exception.getMessage());
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void testGetQuizById_QuizWithNoQuestions() {
        // Arrange
        Quiz emptyQuiz = new Quiz();
        emptyQuiz.setId(3L);
        emptyQuiz.setTitle("Empty Quiz");
        emptyQuiz.setDescription("A quiz with no questions");
        emptyQuiz.setQuestions(List.of());

        when(quizRepository.findById(3L)).thenReturn(Optional.of(emptyQuiz));

        // Act
        QuizDetailsResponse response = quizService.getQuizById(3L);

        // Assert
        assertEquals(3L, response.getId());
        assertEquals("Empty Quiz", response.getTitle());
        assertTrue(response.getQuestions().isEmpty());
    }

    @Test
    void testGetQuizById_QuizWithSingleQuestion() {
        // Arrange
        Quiz singleQuestionQuiz = new Quiz();
        singleQuestionQuiz.setId(2L);
        singleQuestionQuiz.setTitle("Single Question Quiz");
        singleQuestionQuiz.setDescription("One question only");
        singleQuestionQuiz.setQuestions(List.of(question1));

        when(quizRepository.findById(2L)).thenReturn(Optional.of(singleQuestionQuiz));

        // Act
        QuizDetailsResponse response = quizService.getQuizById(2L);

        // Assert
        assertEquals(1, response.getQuestions().size());
        assertEquals("What is the capital of France?", response.getQuestions().get(0).getText());
    }

    // ============= createQuiz Tests =============

    @Test
    void testCreateQuiz_Success() {
        // Arrange
        List<QuestionRequest> questionRequests = List.of(
                new QuestionRequest("What is the capital of France?", List.of("Paris", "London", "Berlin"), 0),
                new QuestionRequest("What is 2+2?", List.of("3", "4", "5"), 1)
        );
        QuizRequest request = new QuizRequest("General Knowledge", "A quiz about general knowledge", questionRequests);

        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("General Knowledge", response.getTitle());
        assertEquals("A quiz about general knowledge", response.getDescription());
        assertEquals(2, response.getQuestions().size());

        verify(quizRepository, times(1)).save(any(Quiz.class));
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateQuiz_WithSingleQuestion() {
        // Arrange
        List<QuestionRequest> questionRequests = List.of(
                new QuestionRequest("Single question?", List.of("Option 1", "Option 2"), 0)
        );
        QuizRequest request = new QuizRequest("Single Q Quiz", "Only one question", questionRequests);

        Quiz singleQuestionQuiz = new Quiz();
        singleQuestionQuiz.setId(5L);
        singleQuestionQuiz.setTitle("Single Q Quiz");
        singleQuestionQuiz.setDescription("Only one question");
        singleQuestionQuiz.setQuestions(List.of(question1));

        when(quizRepository.save(any(Quiz.class))).thenReturn(singleQuestionQuiz);
        when(quizRepository.findById(5L)).thenReturn(Optional.of(singleQuestionQuiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertEquals(5L, response.getId());
        assertEquals("Single Q Quiz", response.getTitle());
        assertEquals(1, response.getQuestions().size());

        verify(quizRepository, times(1)).save(any(Quiz.class));
        verify(quizRepository, times(1)).findById(5L);
    }

    @Test
    void testCreateQuiz_WithMultipleQuestions() {
        // Arrange
        Question question3 = new Question();
        question3.setId(3L);
        question3.setText("Question 3");
        question3.setOptions(List.of("A", "B", "C"));
        question3.setCorrect(2);

        Question question4 = new Question();
        question4.setId(4L);
        question4.setText("Question 4");
        question4.setOptions(List.of("X", "Y", "Z"));
        question4.setCorrect(0);

        List<QuestionRequest> questionRequests = List.of(
                new QuestionRequest("Q1", List.of("A", "B"), 0),
                new QuestionRequest("Q2", List.of("C", "D"), 1),
                new QuestionRequest("Q3", List.of("E", "F"), 0),
                new QuestionRequest("Q4", List.of("G", "H"), 1)
        );
        QuizRequest request = new QuizRequest("Large Quiz", "Multiple questions", questionRequests);

        Quiz largeQuiz = new Quiz();
        largeQuiz.setId(6L);
        largeQuiz.setTitle("Large Quiz");
        largeQuiz.setDescription("Multiple questions");
        largeQuiz.setQuestions(List.of(question1, question2, question3, question4));

        when(quizRepository.save(any(Quiz.class))).thenReturn(largeQuiz);
        when(quizRepository.findById(6L)).thenReturn(Optional.of(largeQuiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertEquals(6L, response.getId());
        assertEquals(4, response.getQuestions().size());

        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void testCreateQuiz_VerifyQuestionMapping() {
        // Arrange - Create specific request to verify exact mapping
        List<QuestionRequest> questionRequests = List.of(
                new QuestionRequest("Capital of Japan?", List.of("Tokyo", "Kyoto", "Osaka"), 0)
        );
        QuizRequest request = new QuizRequest("Geography", "Find capitals", questionRequests);

        Question mappedQuestion = new Question();
        mappedQuestion.setId(10L);
        mappedQuestion.setText("Capital of Japan?");
        mappedQuestion.setOptions(List.of("Tokyo", "Kyoto", "Osaka"));
        mappedQuestion.setCorrect(0);

        Quiz createdQuiz = new Quiz();
        createdQuiz.setId(10L);
        createdQuiz.setTitle("Geography");
        createdQuiz.setDescription("Find capitals");
        createdQuiz.setQuestions(List.of(mappedQuestion));

        mappedQuestion.setQuiz(createdQuiz);

        when(quizRepository.save(any(Quiz.class))).thenReturn(createdQuiz);
        when(quizRepository.findById(10L)).thenReturn(Optional.of(createdQuiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertEquals("Geography", response.getTitle());
        assertEquals("Find capitals", response.getDescription());
        assertEquals(1, response.getQuestions().size());

        QuestionResponse questionResponse = response.getQuestions().get(0);
        assertEquals("Capital of Japan?", questionResponse.getText());
        assertEquals(List.of("Tokyo", "Kyoto", "Osaka"), questionResponse.getOptions());

        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void testCreateQuiz_EmptyQuestions() {
        // Arrange
        List<QuestionRequest> questionRequests = List.of();
        QuizRequest request = new QuizRequest("Empty Quiz", "No questions", questionRequests);

        Quiz emptyQuiz = new Quiz();
        emptyQuiz.setId(11L);
        emptyQuiz.setTitle("Empty Quiz");
        emptyQuiz.setDescription("No questions");
        emptyQuiz.setQuestions(List.of());

        when(quizRepository.save(any(Quiz.class))).thenReturn(emptyQuiz);
        when(quizRepository.findById(11L)).thenReturn(Optional.of(emptyQuiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertEquals(11L, response.getId());
        assertTrue(response.getQuestions().isEmpty());

        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void testCreateQuiz_PreservesQuestionOrder() {
        // Arrange - verify that questions maintain their order
        Question q1 = new Question();
        q1.setId(20L);
        q1.setText("First");
        q1.setOptions(List.of("A", "B"));
        q1.setCorrect(0);

        Question q2 = new Question();
        q2.setId(21L);
        q2.setText("Second");
        q2.setOptions(List.of("C", "D"));
        q2.setCorrect(1);

        Question q3 = new Question();
        q3.setId(22L);
        q3.setText("Third");
        q3.setOptions(List.of("E", "F"));
        q3.setCorrect(0);

        List<QuestionRequest> questionRequests = List.of(
                new QuestionRequest("First", List.of("A", "B"), 0),
                new QuestionRequest("Second", List.of("C", "D"), 1),
                new QuestionRequest("Third", List.of("E", "F"), 0)
        );
        QuizRequest request = new QuizRequest("Ordered Quiz", "Test order", questionRequests);

        Quiz orderedQuiz = new Quiz();
        orderedQuiz.setId(12L);
        orderedQuiz.setTitle("Ordered Quiz");
        orderedQuiz.setDescription("Test order");
        orderedQuiz.setQuestions(List.of(q1, q2, q3));

        when(quizRepository.save(any(Quiz.class))).thenReturn(orderedQuiz);
        when(quizRepository.findById(12L)).thenReturn(Optional.of(orderedQuiz));

        // Act
        QuizDetailsResponse response = quizService.createQuiz(request);

        // Assert
        assertEquals(3, response.getQuestions().size());
        assertEquals("First", response.getQuestions().get(0).getText());
        assertEquals("Second", response.getQuestions().get(1).getText());
        assertEquals("Third", response.getQuestions().get(2).getText());
    }
}