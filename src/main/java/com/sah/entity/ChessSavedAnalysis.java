package com.sah.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChessSavedAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idAnalysis;

    @Column(nullable = false)
    public String startingFEN;
    @Column(nullable = false)
    public String PGN;
    @Column(nullable = false)
    public LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    public Users userId;
}
