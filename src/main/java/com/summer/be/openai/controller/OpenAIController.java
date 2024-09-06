package com.summer.be.openai.controller;


import com.summer.be.openai.entity.Learnings;
import com.summer.be.openai.service.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ApiResponse(
            responseCode = "500",
            description = "문장 생성에 실패하였습니다."
    )
    @ResponseBody
    @PostMapping("/saveLearnings")
    public ResponseEntity<?> saveLearnings() {
        try {
            // OpenAI로부터 추천 주제 받기
            String recommendedPhrase = openAIService.getRecommendedPhrase();
            log.info("my recommend phrase is '" + recommendedPhrase + "'");

            // 추천 주제로 문장 및 단어 생성
            List<String> sentences = openAIService.getSentencesUsingPhrase(recommendedPhrase);
            List<String> voca = openAIService.getVocabularyUsingPhrase(recommendedPhrase);

            // 데이터베이스에 학습 정보 저장
            Learnings learnings = openAIService.saveLearning(recommendedPhrase);
            openAIService.saveVoca(voca, learnings);
            openAIService.saveSentences(sentences, learnings);

            return ResponseEntity.ok("ok");

        } catch (DataAccessException e) {
            // 데이터베이스 접근 실패 처리
            log.error("데이터베이스 접근 실패: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터 저장 중 오류가 발생했습니다.");

        } catch (Exception e) {
            // 그 외 모든 예외 처리
            log.error("문장 생성 중 예기치 못한 오류가 발생했습니다: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문장 생성 중 예기치 못한 오류가 발생했습니다.");
        }
    }


    @Operation(
            summary = "추천 내용",
            description = "생성된 오늘의 추천을 보여드립니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success 추천 Get"
    )
    @GetMapping("/getLearnings")
    @ResponseBody
    public String getLearnings() {
        String topic = openAIService.getLearnings();
        return topic;
    }


    @Operation(
            summary = "Get Vocabularies",
            description = "생성된 오늘의 단어들을 보여드립니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success Voca Get"
    )
    @GetMapping("/getVocabulary")
    public List<String> getVocabulary() {
        return openAIService.getVocabulary();
    }

    @Operation(
            summary = "Get Sentences",
            description = "생성된 오늘의 문장들을 보여드립니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success Sentences Get"
    )
    @GetMapping("/getSentences")
    public List<String> getSentences() {
        return openAIService.getSentences();
    }

    @Operation(
            summary = "Get key",
            description = "키 값 확인용"
    )
    @ApiResponse(
            responseCode = "200",
            description = "key get"
    )
    @GetMapping("/getKey")
    public String key() {
        return openAIService.returnApiKey();
    }
}