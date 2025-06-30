package com.hongik.genieary.auth.service;

import com.hongik.genieary.auth.dto.request.LoginRequest;
import com.hongik.genieary.auth.dto.request.SignupRequest;
import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.entity.RefreshToken;
import com.hongik.genieary.auth.jwt.JwtUtil;
import com.hongik.genieary.auth.repository.JwtBlacklistRepository;
import com.hongik.genieary.auth.repository.RefreshTokenRepository;
import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.enums.LoginType;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    //TODO : 이메일 인증
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;

    /**
     회원가입
     */
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GeneralException(ErrorStatus.AUTH_EMAIL_ALREADY_EXISTS);
        }
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new GeneralException(ErrorStatus.AUTH_PASSWORD_MISMATCH);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .loginType(LoginType.NORMAL)
                .build();
        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        // Redis에 저장
        refreshTokenRepository.save(user.getEmail(), refreshToken, jwtUtil.getRefreshTokenExpirationMillis());

        return TokenResponse.builder()
                .userId(savedUser.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
    로그인
     */
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.AUTH_USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_PASSWORD);
        }
        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // Redis에 저장
        refreshTokenRepository.save(user.getEmail(), refreshToken, jwtUtil.getRefreshTokenExpirationMillis());

        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    /**
     토큰 재발급
     */
    public TokenResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);

        String savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.AUTH_INVALID_REFRESH_TOKEN));

        if (!savedToken.equals(refreshToken)) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.AUTH_USER_NOT_FOUND));

        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        refreshTokenRepository.save(email, newRefreshToken, jwtUtil.getRefreshTokenExpirationMillis());

        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * 이메일 중복 확인 로직 구현
     */
    public boolean checkEmailAvailability(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus.VALIDATION_ERROR);
        }

        return !userRepository.existsByEmail(email);
    }
  
    /**
     *로그아웃
     */
    public void logout(String accessToken) {
        String email = jwtUtil.getEmailFromToken(accessToken);

        // Redis에서 RefreshToken 삭제
        refreshTokenRepository.deleteByEmail(email);

        // accessToken 만료 시간 계산
        long expirationMillis = jwtUtil.getExpiration(accessToken) - System.currentTimeMillis();
        if (expirationMillis > 0) {
            jwtBlacklistRepository.save(accessToken, expirationMillis);
        }
    }
}
