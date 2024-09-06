package com.summer.be.openai.service;


import com.google.gson.Gson;
import com.summer.be.openai.dao.LearningsRepository;
import com.summer.be.openai.dao.SentencesRepository;
import com.summer.be.openai.dao.VocabularyRepository;
import com.summer.be.openai.dto.*;

import com.summer.be.openai.entity.Learnings;
import com.summer.be.openai.entity.Sentences;
import com.summer.be.openai.entity.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.api-key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";  // OpenAI 호출 URL
    private final LearningsRepository learningsRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SentencesRepository sentencesRepository;

    public String getRecommendedPhrase() {
        return getCompletion("Can you recommend a topic for daily English practice?");  // 하루에 어떤 주제를 선정할지 추천해줍니다.
    }

    public List<String> getSentencesUsingPhrase(String phrase) {
        String prompt = String.format("Generate 2 sentences using the topic '%s' in english.", phrase);
        String response = getCompletion(prompt);

        String[] sentences = response.split("\n");
        List<String> sentenceList = new ArrayList<>();
        Collections.addAll(sentenceList, sentences);

        // List 10개 안될 시, 이쪽 부근에 조건문 시작
        return sentenceList;
    }

    public List<String> getVocabularyUsingPhrase(String phrase) {
        String prompt = String.format("Generate 2 vocabularies using the topic '%s'.", phrase);
        String response = getCompletion(prompt);

        String[] vocabulary = response.split("\n");
        List<String> vocaList = new ArrayList<>();
        Collections.addAll(vocaList, vocabulary);

        // List 10개 안될 시, 이쪽 부근에 조건문 시작
        return vocaList;
    }

    private String getCompletion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");

        Message userMessage = new Message("user", prompt);
        OpenAIRequest request = new OpenAIRequest("ft:gpt-4o-mini-2024-07-18:personal::9zfcDZXf", Collections.singletonList(userMessage));

        try {
            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIResponse> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, entity, OpenAIResponse.class);

            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            } else {
                return "No response from OpenAI.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    public Learnings saveLearning(String recommendedPhrase) {
        LearningsDto learningsDto = new LearningsDto(recommendedPhrase);
        Learnings openAI = learningsDto.toEntity();
        learningsRepository.save(openAI);

        return openAI;
    }   // List 10개가 안될 시 재 요청

    public void saveVoca(List<String> voca, Learnings learnings) {
        VocabularyDto vocabularyDto = new VocabularyDto(voca, learnings);
        Vocabulary saveVoca = vocabularyDto.toEntity();
        vocabularyRepository.save(saveVoca);
    }   // List 10개가 안될 시 재 요청

    public void saveSentences(List<String> sentences, Learnings learnings) {
        SentencesDto sentencesDto = new SentencesDto(sentences, learnings);
        Sentences saveSentences = sentencesDto.toEntity();
        sentencesRepository.save(saveSentences);
    }

    public List<Learnings> findOpenAI() {
        return learningsRepository.findAll();
    }

    public String getLearnings() {
        Long id = learningsRepository.findTopIdByOrderByIdDesc(); // 이건 나중에 분리하자. voca와 sentence 가져올 때도 id 이용해야함
        String topic = learningsRepository.findTopicById(id);
        return topic;
    }

    public List<String> getVocabulary() {
        // 가장 최근에 저장된 Vocabulary ID를 가져옴
        Long id = learningsRepository.findTopIdByOrderByIdDesc();

        // ID로 Vocabulary 조회
        String vocabularyJson = vocabularyRepository.findVocabularyById(id);

        // JSON 형태로 저장된 Vocabulary 문자열을 List<String> 형태로 변환
        Gson gson = new Gson();
        List<String> vocabularyList = gson.fromJson(vocabularyJson, List.class);

        return vocabularyList;
    }   // 저장된 Voca 가져오기


    public List<String> getSentences() {
        // 가장 최근에 저장된 Vocabulary ID를 가져옴
        Long id = learningsRepository.findTopIdByOrderByIdDesc();

        // ID로 Sentences 조회
        String sentencesJson = sentencesRepository.findSentencesById(id);

        // JSON 형태로 저장된 sentences 문자열을 List<String> 형태로 변환
        Gson gson = new Gson();
        List<String> sentencesList = gson.fromJson(sentencesJson, List.class);

        return sentencesList;
    }
}
