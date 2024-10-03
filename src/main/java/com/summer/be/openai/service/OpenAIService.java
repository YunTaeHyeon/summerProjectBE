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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        List<String> sentenceList = new ArrayList<>();

        String prompt = String.format("Generate 10 sentences using the topic '%s'.", phrase);
        String response = getCompletion(prompt);

        String[] sentences = response.split("\n");
        Collections.addAll(sentenceList, sentences);
        return sentenceList;
    }

    public List<String> getVocabularyUsingPhrase(String phrase) {
        List<String> vocaList = new ArrayList<>();
        String prompt = String.format("Generate 10 vocabularies using the topic '%s'.", phrase);
        String response = getCompletion(prompt);

        String[] vocabulary = response.split("\n");
        Collections.addAll(vocaList, vocabulary);

        return vocaList;
    }

    private String getCompletion(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");

        Message userMessage = new Message("user", prompt);
        OpenAIRequest request = new OpenAIRequest("ft:gpt-4o-mini-2024-07-18:personal:english-1001:ADT0KRUy", Collections.singletonList(userMessage));

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

    public Map<String, String> getVocabulary() {
        // 가장 최근에 저장된 Vocabulary ID를 가져옴
        Long id = learningsRepository.findTopIdByOrderByIdDesc();

        // ID로 Vocabulary 조회
        String vocabularyJson = vocabularyRepository.findVocabularyById(id);

        // JSON 형태로 저장된 Vocabulary 문자열을 List<String> 형태로 변환
        Gson gson = new Gson();
        List<String> vocabularyList = gson.fromJson(vocabularyJson, List.class);

        Map<String, String> vocaData = new HashMap<>();

        for(String s: vocabularyList) {
            String example = getExampleSentence(s);
            vocaData.put(s, example);
        }

        return vocaData;
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

    public String getTranslate(String translate) {
        String prompt = String.format("Please translate '%s' into Korean.", translate);
        // Please translate 'Different dogs come in various breeds, like Labrador or Beagle.' into Korean.
        String response = getCompletion(prompt);

        return response;
    }

    public String getExampleSentence(String vocabulary) {
        String prompt = String.format("Please make one English sentence containing ‘%s’.", vocabulary);

        String response = getCompletion(prompt);
        // Please make one English sentence containing ‘Voucher’.
        String responseTrans = getTranslate(response);  // 영단어로 만들어진 영어 문장은 다시 한국어로 번형합니다.

        return responseTrans;
    }   // voca를 활용한 예문 문장 만들기 입니다.
    // 예를 들어 보카 단어가 나오면 맞추어야 하는데, 보카가 포함된 문장을 생성 후 한국어로 번형합니다. 그 한국어 문장을 확인 후 영어 단어를 유추합니다.

    public Map<String, String> getAnswerHint(String answer, String userAnswer) {
        Map<String, String> result = new HashMap<>();

        if (answer.equals(userAnswer)) {
            result.put("correct", "정답입니다.");
        } else {
            String prompt = String.format("The correct answer is '%s', but I answered '%s'.", answer, userAnswer);
            result.put("wrong", prompt); // 실제 답변과 사용자의 답변을 대조 (이 부분 파인튜닝 작업 필요)
        }

        return result;
    }
}