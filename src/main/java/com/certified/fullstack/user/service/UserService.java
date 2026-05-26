package com.certified.fullstack.user.service;

import com.certified.fullstack.user.dto.UserRequest;
import com.certified.fullstack.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    List<UserResponse> getAllUsers();
}