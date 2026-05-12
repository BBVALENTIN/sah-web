package com.sah.entity;

import jakarta.persistence.*;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "post_id"})
        }
) // un user poate da doar un singur like la o postare

@Entity
public class PostsLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Posts postId;
}
