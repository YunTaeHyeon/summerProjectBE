package com.summer.be.openai.controller;


import com.summer.be.openai.entity.Learnings;
import com.summer.be.openai.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "OpenAI", description = "OpenAI 관련 API 입니다.")
@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @Operation(
            summary = "문장 생성",
            description = "오늘의 추천 주제 기반으로 문장 10개를 생성합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "문장 생성에 성공하였습니다."
    )
    @ResponseBody
    @PostMapping("/saveLearnings")
    public String saveLearnings() {
        String recommendedPhrase = openAIService.getRecommendedPhrase();
        List<String> sentences = openAIService.getSentencesUsingPhrase(recommendedPhrase);
        List<String> voca = openAIService.getVocabularyUsingPhrase(recommendedPhrase);
        log.info("my recommend phrase is " + "'" + recommendedPhrase + "'");
        Learnings learnings = openAIService.saveLearning(recommendedPhrase);
        openAIService.saveVoca(voca, learnings);
        openAIService.saveSentences(sentences, learnings);

        return "ok";
    }

    @Operation(
            summary = "추천 내용",
            description = "생성된 오늘의 추천을 보여드립니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success 추천 Get"
    )
    public List<Learnings> getLearnings() {
        List<Learnings> learnings = openAIService.getLearnings();
        return learnings;
    }
}