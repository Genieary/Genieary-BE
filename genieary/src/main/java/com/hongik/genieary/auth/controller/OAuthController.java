package com.hongik.genieary.auth.controller;

import com.hongik.genieary.auth.dto.response.TokenResponse;
import com.hongik.genieary.auth.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "OAuth API", description = "소셜 로그인(카카오) 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final KakaoOAuthService kakaoOAuthService;


    @Operation(
            summary = "카카오 로그인 콜백",
            description = """
            카카오 인가코드(code)를 받아 JWT 토큰을 발급합니다.<br>
            프론트엔드는 카카오 로그인 후 리다이렉트된 URL에서 code를 추출해 이 API로 전달해야 합니다.<br>
            <b>예시 요청:</b> <br>
            <code>/api/auth/kakao?code=인가코드</code>
            """,
            parameters = {
                    @Parameter(
                            name = "code",
                            description = "카카오 인가 코드",
                            required = true,
                            example = "QZ2wQw1xQ2t2d9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공, JWT 토큰 반환",
                            content = @Content(
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(
                                            name = "카카오 로그인 성공 예시",
                                            value = """
                        {
                          "userId": 1,
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "카카오 로그인 실패(인가코드 오류, 동의 항목 누락 등)",
                            content = @Content(
                                    schema = @Schema(
                                            example = """
                        {
                          "isSuccess": false,
                          "code": "A400",
                          "message": "카카오 로그인에 실패했습니다.",
                          "result": null
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam String code) {
        TokenResponse tokenResponse = kakaoOAuthService.kakaoLogin(code);
        return ResponseEntity.ok(tokenResponse);
    }
}
