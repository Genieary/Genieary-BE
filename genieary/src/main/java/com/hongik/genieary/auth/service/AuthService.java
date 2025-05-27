package com.hongik.genieary.auth.service;

import com.hongik.genieary.auth.dto.request.LoginRequest;
import com.hongik.genieary.auth.dto.request.SignupRequest;
import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.entity.RefreshToken;
import com.hongik.genieary.auth.jwt.JwtUtil;
import com.hongik.genieary.auth.repository.RefreshTokenRepository;
import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                .build();
        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(savedUser.getEmail())
                        .refreshToken(refreshToken)
                        .build()
        );

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

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(user.getEmail())
                        .refreshToken(refreshToken)
                        .build()
        );

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

        RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() ->new GeneralException(ErrorStatus.AUTH_INVALID_REFRESH_TOKEN));

        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.AUTH_USER_NOT_FOUND));

        String newAccessToken = jwtUtil.generateToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        savedToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
