package com.summer.be.openai.dao;

import com.summer.be.openai.entity.Sentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SentencesRepository extends JpaRepository<Sentences, Long> {
    // 최근에 저장된 vocabulary의 영어 단어 리스트를 가져오는 쿼리
    @Query("SELECT s.english_sentence FROM Sentences s WHERE s.id = :id")
    String findSentencesById(Long id);
}
