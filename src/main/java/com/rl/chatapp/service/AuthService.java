package com.rl.chatapp.service;

import com.rl.chatapp.dto.AuthResponse;
import com.rl.chatapp.dto.SignupRequest;
import com.rl.chatapp.model.RefreshToken;
import com.rl.chatapp.model.User;
import com.rl.chatapp.repository.UserRepository;
import com.rl.chatapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(savedUser.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }
}
