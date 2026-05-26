package com.certified.fullstack.user.service.impl;

import com.certified.fullstack.user.dto.UserRequest;
import com.certified.fullstack.user.dto.UserResponse;
import com.certified.fullstack.user.entity.User;
import com.certified.fullstack.user.repository.UserRepository;
import com.certified.fullstack.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User savedUser = userRepository.save(user);

        return new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
                ))
                .toList();
    }
}