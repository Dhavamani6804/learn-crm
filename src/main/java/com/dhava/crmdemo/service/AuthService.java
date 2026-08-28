package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.LoginRequest;
import com.dhava.crmdemo.dto.response.LoginResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.User;
import com.dhava.crmdemo.mapper.UserMapper;
import com.dhava.crmdemo.repository.UserRepository;
import com.dhava.crmdemo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        UserResponse userResponse = userMapper.toUserResponse(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                userResponse
        );
    }
}