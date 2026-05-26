package com.certified.fullstack.quiz.service.impl;

import com.certified.fullstack.exception.NotFoundException;
import com.certified.fullstack.quiz.dto.response.QuestionResponse;
import com.certified.fullstack.quiz.dto.response.QuizDetailsResponse;
import com.certified.fullstack.quiz.dto.request.QuizRequest;
import com.certified.fullstack.quiz.dto.response.QuizSummaryResponse;
import com.certified.fullstack.quiz.entity.Question;
import com.certified.fullstack.quiz.entity.Quiz;
import com.certified.fullstack.quiz.repository.QuizRepository;
import com.certified.fullstack.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    /*
            Retrieve all available quizzes (id, title, description only) --- OK ---
     */
    @Override
    public List<QuizSummaryResponse> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(quiz -> new QuizSummaryResponse(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getDescription()
                ))
                .toList();
    }

    /*
        Get full quiz details, including all questions and options (but NOT correct answers) --- OK ---
     */
    @Override
    public QuizDetailsResponse getQuizById(Long id) {

        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Quiz not found"));

        return new QuizDetailsResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getQuestions()
                        .stream()
                        .map(question -> new QuestionResponse(
                                question.getId(),
                                question.getText(),
                                question.getOptions()
                        ))
                        .toList()
        );
    }
    /*
            Create a new quiz --- OK ---
     */
    @Transactional
    @Override
    public QuizDetailsResponse createQuiz(QuizRequest request) {

        Quiz quiz = new Quiz();

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());

        List<Question> questions = request.getQuestions()
                .stream()
                .map(q -> {
                    Question question = new Question();

                    question.setText(q.getText());
                    question.setOptions(q.getOptions());
                    question.setCorrect(q.getCorrect());
                    question.setQuiz(quiz);

                    return question;
                })
                .toList();

        quiz.setQuestions(questions);

        Quiz savedQuiz = quizRepository.save(quiz);

        return getQuizById(savedQuiz.getId());
    }
}