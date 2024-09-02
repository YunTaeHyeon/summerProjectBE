package com.summer.be.openai.dao;


import com.summer.be.openai.entity.Learnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningsRepository extends JpaRepository<Learnings, Long> {
    @Query(value = "select learnings_id from learnings ORDER BY learnings_id desc limit 1", nativeQuery = true )
    Long findTopIdByOrderByIdDesc();

    @Query("SELECT l.topic FROM Learnings l WHERE l.id = :id")
    String findTopicById(Long id);
}
