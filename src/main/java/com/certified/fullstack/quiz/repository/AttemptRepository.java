package com.certified.fullstack.quiz.repository;

import com.certified.fullstack.quiz.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    boolean existsByQuizIdAndUserIdAndCompletedAtIsNull(
            Long quizId,
            Long userId
    );
    List<Attempt> findAllByUserId(Long userId);
}