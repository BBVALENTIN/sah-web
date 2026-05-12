package com.sah.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users creator;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private Long likes_number;

    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}
