package com.summer.be.member.controller;

import com.summer.be.member.domain.dto.KakaoDto;
import com.summer.be.member.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Tag(name = "User", description = "User 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class KakaoController {
    private final KakaoService kakaoService;

    @Operation(
            summary = "회원가입",
            description = "회원가입을 합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "회원가입에 성공하였습니다."
    )

    @RequestMapping("/login/oauth2/code/kakao")
    public String kakaoLogin(@RequestParam("code") String code) throws IOException {
        // 1. 인가 코드 받기 (@RequestParam String code)

        // 2. 토큰 받기
        String accessToken = kakaoService.getAccessToken(code);

        // 3. 사용자 정보 받기
        KakaoDto userInfo = kakaoService.getUserInfo(accessToken);

        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();

        System.out.println("email = " + email);
        System.out.println("nickname = " + nickname);
        System.out.println("accessToken = " + accessToken);

        return "redirect:/result";
    }

    @GetMapping("/kakaologin")
    public String loginForm(Model model){
        /*kakaoService.kakaoMethod();
        System.out.println(kakaoService.getKakaoApiKey());
        System.out.println(kakaoService.getKakaoRedirectUri());
        model.addAttribute("kakaoApiKey", kakaoService.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoService.getKakaoRedirectUri());*/
        return "login";
    }
}
