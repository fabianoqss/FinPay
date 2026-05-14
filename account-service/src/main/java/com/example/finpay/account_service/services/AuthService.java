package com.example.finpay.account_service.services;

import com.example.finpay.account_service.config.jwt.JwtService;
import com.example.finpay.account_service.dto.auth.LoginRequest;
import com.example.finpay.account_service.dto.auth.LoginResponse;
import com.example.finpay.account_service.entities.User;
import com.example.finpay.account_service.enums.UserStatus;
import com.example.finpay.account_service.repositories.UserRepository;
import com.example.finpay.account_service.services.exceptions.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                user.getId(),
                user.getEmail()
        );
    }
}
