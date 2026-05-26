package com.certified.fullstack.quiz.repository;

import com.certified.fullstack.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> { }