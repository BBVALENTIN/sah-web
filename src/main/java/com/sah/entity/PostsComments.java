package com.sah.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PostsComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @Column(nullable = false, length = 250)
    private String content;

    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}
