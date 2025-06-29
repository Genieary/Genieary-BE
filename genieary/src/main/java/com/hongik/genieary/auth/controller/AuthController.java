package com.hongik.genieary.auth.controller;

import com.hongik.genieary.auth.dto.request.LoginRequest;
import com.hongik.genieary.auth.dto.request.SignupRequest;
import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                    value = "{\"email\": \"test@example.com\", \"password\": \"1234\", \"passwordCheck\": \"1234\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "회원가입 성공 예시",
                                            value = """
                        {
                          "isSuccess": true,
                          "code": "200",
                          "message": "회원가입 성공",
                          "result": {
                            "userId": 1,
                            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                          }
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류 등",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "회원가입 실패 예시",
                                            value = """
                        {
                          "isSuccess": false,
                          "code": "VALID401",
                          "message": "이메일 형식이 올바르지 않습니다.",
                          "result": null
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @org.springframework.web.bind.annotation.RequestBody SignupRequest request) {
       TokenResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
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
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공, 토큰 반환",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "로그인 성공 예시",
                                            value = """
                                                    {
                                                      "isSuccess": true,
                                                      "code": "S200",
                                                      "message": "로그인 성공",
                                                      "result": {
                                                        "userId": 1,
                                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류 등",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "로그인 실패 예시",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "AUTH4004",
                                                      "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            )
                    )
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

    @Operation(
            summary = "로그아웃",
            description = """
            JWT 로그아웃을 수행합니다.  
            Authorization 헤더에 AccessToken을 Bearer 타입으로 전달하세요.
            """,
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer 타입의 AccessToken (예: Bearer eyJhbGciOi...)",
                            required = true,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공 (No Content)"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 또는 토큰 오류")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // "Bearer {token}"에서 token만 추출
        String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }
}