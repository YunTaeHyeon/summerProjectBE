package com.summer.be.openai.dao;


import com.summer.be.openai.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    // 최근에 저장된 vocabulary의 영어 단어 리스트를 가져오는 쿼리
    @Query("SELECT v.vocabulary FROM Vocabulary v WHERE v.id = :id")
    String findVocabularyById(Long id);
}
