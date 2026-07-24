package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENTS_LIKES_POSTS_COMMENTS"))
    private PostsComments comment;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_COMMENTS_LIKES_USERS"))
    private Users user;

    public CommentsLikes(PostsComments comment, Users user)
    {
        this.comment = comment;
        this.user = user;
    }
}
