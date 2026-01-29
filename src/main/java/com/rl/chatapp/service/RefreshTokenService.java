package com.rl.chatapp.service;

import com.rl.chatapp.dto.AuthResponse;
import com.rl.chatapp.dto.RefreshTokenRequest;
import com.rl.chatapp.model.RefreshToken;
import com.rl.chatapp.model.User;
import com.rl.chatapp.repository.RefreshTokenRepository;
import com.rl.chatapp.repository.UserRepository;
import com.rl.chatapp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(15)));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException("Refresh token expired");
        }
        return refreshToken;
    }

    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .map(this::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(accessToken, refreshTokenRequest.getRefreshToken());
    }
}
