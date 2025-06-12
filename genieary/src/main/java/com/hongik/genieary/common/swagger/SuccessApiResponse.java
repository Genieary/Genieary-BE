package com.hongik.genieary.common.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공 응답",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                        name = "onSuccess",
                        summary = "기본 성공 응답",
                        value = """
                    {
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "성공입니다."
                    }
                    """
                )
        )
)
public @interface SuccessApiResponse {}
