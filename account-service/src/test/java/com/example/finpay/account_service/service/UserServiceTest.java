package com.example.finpay.account_service.service;

import com.example.finpay.account_service.dto.UserRequest;
import com.example.finpay.account_service.dto.UserResponse;
import com.example.finpay.account_service.entities.User;
import com.example.finpay.account_service.enums.UserStatus;
import com.example.finpay.account_service.repositories.UserRepository;
import com.example.finpay.account_service.services.UserService;
import com.example.finpay.account_service.services.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWithActiveStatus() {
        UserRequest request = new UserRequest("Fabiano", "fabiano@email.com", "123.456.789-00");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserResponse response = userService.createUser(request);

        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.email()).isEqualTo("fabiano@email.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById("invalid-id"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("invalid-id");
    }

    @Test
    void shouldBlockUser() {

        User user = User.builder()
                .id("usr-123")
                .status(UserStatus.ACTIVE)
                .build();

        when(repository.findById("usr-123")).thenReturn(Optional.of(user));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        userService.blockUser("usr-123");
        
        verify(repository).save(argThat(u -> u.getStatus() == UserStatus.BLOCKED));
    }
}
