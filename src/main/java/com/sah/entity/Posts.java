package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private int likes_number = 0;

    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    public Posts(String content, Users user) {
        this.content = content;
        this.creator = user;
    }
}
