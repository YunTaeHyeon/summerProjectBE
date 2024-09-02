package com.summer.be.member.controller;

import com.summer.be.member.domain.Member;
import com.summer.be.member.domain.dto.KakaoAccountIdDto;
import com.summer.be.member.domain.dto.LoginResponseDto;
import com.summer.be.member.domain.dto.TokenRequestAndResponseDto;
import com.summer.be.member.service.MemberService;
import com.summer.be.security.jwt.AuthTokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "User 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RequestMapping("/api/user")
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "로그인",
            description = "카카오 계정 아이디로 로그인을 합니다. 계정 정보가 없을 경우 생성합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인에 성공하였습니다."
    )

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoAccountIdDto kakaoAccountIdDto) {
        LoginResponseDto loginResponseDto = memberService.login(kakaoAccountIdDto.getKakaoAccountId());

        return ResponseEntity.ok(loginResponseDto);
    }

    @Operation(
            summary = "Apache Test",
            description = "Test about Apache"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success of Apache"
    )
    @GetMapping("/apache-test")
    public ResponseEntity<?> apacheTest() {
        List<Member> findEmail = memberService.findAll();
        if (findEmail.isEmpty()) {
            log.info("회원정보가 비어있습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No members found");
        } else return ResponseEntity.ok(findEmail);
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Access Token과 Refresh Token을 이용하여 새로운 Access Token을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "토큰 재발급에 성공하였습니다."
    )

    @PostMapping("/reissue")
    public ResponseEntity<TokenRequestAndResponseDto> reissue(@RequestBody TokenRequestAndResponseDto tokenRequestDto) {
        AuthTokens authTokens = memberService.reissue(tokenRequestDto);

        TokenRequestAndResponseDto tokenRequestAndResponseDto = TokenRequestAndResponseDto.builder()
                .accessToken(authTokens.getAccessToken())
                .refreshToken(authTokens.getRefreshToken())
                .build();

        return ResponseEntity.ok(tokenRequestAndResponseDto);
        //return ResponseEntity.ok(memberService.reissue(tokenRequestDto));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody TokenRequestAndResponseDto tokenRequestDto) {
        memberService.logout(tokenRequestDto);
    }
}