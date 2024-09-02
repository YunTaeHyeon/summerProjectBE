package com.summer.be.openai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Learnings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Learnings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learnings_id")
    private Long id;

    @Column(name = "topic", columnDefinition = "LONGTEXT", nullable = false)
    private String topic;

    @OneToOne(mappedBy = "learnings", cascade = CascadeType.ALL, orphanRemoval = true)
    private Sentences sentences;

    @OneToOne(mappedBy = "learnings", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vocabulary vocabulary;

    private int status;
}
