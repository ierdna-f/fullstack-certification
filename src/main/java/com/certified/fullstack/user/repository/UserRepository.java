package com.certified.fullstack.user.repository;

import com.certified.fullstack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { }