package com.hongik.genieary.auth.controller;

import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam String code) {
        TokenResponse tokenResponse = kakaoOAuthService.kakaoLogin(code);
        return ResponseEntity.ok(tokenResponse);
    }
}
