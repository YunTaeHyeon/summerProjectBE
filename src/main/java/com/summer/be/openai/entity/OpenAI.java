package com.summer.be.openai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OpenAI")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic", columnDefinition = "LONGTEXT", nullable = false)
    private String topic;

    @Column(name = "English-sentence", columnDefinition = "LONGTEXT")
    private String sentences;

    @Column(name = "English-vocabulary", columnDefinition = "LONGTEXT")
    private String vocabulary;
}
