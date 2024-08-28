package com.summer.be;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "Test API 입니다. JWT 인증이 없으면 접근할 수 없습니다.")
@RestController
@CrossOrigin
@RequestMapping("/api/test")
public class TestController {

    @Operation(
            summary = "Test",
            description = "Test"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Test"
    )

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }
}
