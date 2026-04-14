package com.example.finpay.account_service.services;

import com.example.finpay.account_service.dto.UpdateUserRequest;
import com.example.finpay.account_service.dto.UserRequest;
import com.example.finpay.account_service.dto.UserResponse;
import com.example.finpay.account_service.entities.User;
import com.example.finpay.account_service.enums.UserStatus;
import com.example.finpay.account_service.repositories.UserRepository;
import com.example.finpay.account_service.services.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse findById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return UserResponse.from(user);
    }

    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(request.name());
        user.setEmail(request.email());
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);

        return UserResponse.from(updatedUser);
    }

    public UserResponse createUser(UserRequest userRequest){
        User user = User.builder().id(UUID.randomUUID().toString())
                .email(userRequest.email())
                .name(userRequest.name())
                .cpf(userRequest.cpf())
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }


    public void blockUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(UserStatus.BLOCKED);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }


    public void activateUser(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }


}
