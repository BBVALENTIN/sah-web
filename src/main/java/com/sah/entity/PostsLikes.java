package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "post_id"})
        }
) // un user poate da doar un singur like la o postare

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostsLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_POSTS_LIKES_USERS"))
    private Users user;

    @ManyToOne
    @JoinColumn(name="post_id", foreignKey = @ForeignKey(name = "FK_POSTS_LIKES_POSTS"))
    private Posts post;

    public PostsLikes(Users users, Posts posts) {
        this.user = users;
        this.post = posts;
    }
}
