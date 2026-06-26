package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Nationalized
    private String content;

    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private long likesNumber;

    @Column(nullable = false)
    private boolean deleted = false;

    public PostsComments(Users user, Posts post, String content)
    {
        this.user = user;
        this.post = post;
        this.content = content;
    }
}
