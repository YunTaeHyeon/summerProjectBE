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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "OpenAI", description = "OpenAI 관련 API 입니다.")
@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @Operation(
            summary = "오늘의 수업 생성 부분입니다",
            description = "이 부분을 클릭하면 오늘의 주제와 단어, 문장을 생성합니다. [Post 요청 후 오늘의 문장 및 단어 확인 가능]"
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
            summary = "오늘의 주제",
            description = "생성된 오늘의 주제입니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success 추천 Get"
    )
    @GetMapping("/getLearnings")
    @ResponseBody
    public Map<String, String> getLearnings() {
        String topic = openAIService.getLearnings();
        Map<String, String> response = new HashMap<>();
        response.put("topic", topic);
        return response;
    }


    @Operation(
            summary = "단어 수업",
            description = "오늘 생성한 추천을 기반으로 단어 10개를 생성합니다.(With 예문 문장)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success Voca Get"
    )
    @GetMapping("/getVocabulary")
    public Map<String, String> getVocabulary() {
        Map<String, String> voca = openAIService.getVocabulary();
        return voca;
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

    /*@Operation(
            summary = "translate",
            description = "생성된 단어로 만들어진 예문 영어의 번역과 영어 문장 번역이 생성됩니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Success translate"
    )
    @GetMapping("/translate")
    public Map<String, String> translate(@RequestParam("translate") String translate) {
        String korean = openAIService.getTranslate(translate);
        Map<String, String> response = new HashMap<>();
        response.put("translate", korean);
        return response;
    } */  // 번역 용, 왠지 사용 안할 것 같습니다.
}