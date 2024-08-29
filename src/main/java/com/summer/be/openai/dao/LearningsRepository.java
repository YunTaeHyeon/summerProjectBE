package com.summer.be.openai.dao;


import com.summer.be.openai.entity.Learnings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningsRepository extends JpaRepository<Learnings, Long> {
    @Query("SELECT u.id FROM Learnings u ORDER BY u.id DESC LIMIT 1")
    Long findTopIdByOrderByIdDesc();
    String findTopicById(Long id);
}
