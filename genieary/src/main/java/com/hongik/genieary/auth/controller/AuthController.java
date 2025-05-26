package com.hongik.genieary.auth.controller;

import com.hongik.genieary.auth.dto.request.LoginRequest;
import com.hongik.genieary.auth.dto.request.SignupRequest;
import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Auth API", description = "회원가입, 로그인, 토큰 재발급 등 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호로 회원가입을 진행합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(
                                    name = "회원가입 예시",
                                    value = "{\"email\": \"test@example.com\", \"password\": \"1234\", \"passwordConfirm\": \"1234\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "입력값 오류 등")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@org.springframework.web.bind.annotation.RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(
            summary = "로그인",
            description = "이메일, 비밀번호로 로그인 후 JWT 토큰을 발급받습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "로그인 예시",
                                    value = "{\"email\": \"test@example.com\", \"password\": \"1234\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공, 토큰 반환",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "입력값 오류 등")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 이용해 새로운 액세스/리프레시 토큰을 발급받습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"refreshToken\": \"your-refresh-token\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "토큰 오류 등")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> reissue(@org.springframework.web.bind.annotation.RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        TokenResponse tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok(tokens);
    }
}