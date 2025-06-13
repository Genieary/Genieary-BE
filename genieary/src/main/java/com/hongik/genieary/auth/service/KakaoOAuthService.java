package com.hongik.genieary.auth.service;

import com.hongik.genieary.auth.dto.request.KakaoUserInfo;
import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.jwt.JwtUtil;
import com.hongik.genieary.auth.repository.RefreshTokenRepository;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public TokenResponse kakaoLogin(String code) {
        // 인가코드로 토큰 요청
        String accessToken = getKakaoAccessToken(code);

        // accessToken으로 사용자 정보 요청
        KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);

        // DB에 사용자 확인/회원가입
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.getEmail())
                            .nickname(userInfo.getNickname())
                            // 소셜 로그인 회원은 랜덤 비밀번호를 암호화해서 저장
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .build();
                    return userRepository.save(newUser);
                });

        // JWT 토큰 발급
        String jwtAccessToken = jwtUtil.generateToken(user.getEmail());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        refreshTokenRepository.save(user.getEmail(), jwtRefreshToken, jwtUtil.getRefreshTokenExpirationMillis());

        return TokenResponse.builder()
                .userId(user.getId())
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    // 카카오 토큰 요청
    private String getKakaoAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    // 카카오 사용자 정보 요청
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();

        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");

        return new KakaoUserInfo(email, nickname);
    }
}