package com.sah.repository;

import com.sah.entity.CommentsLikes;
import com.sah.entity.PostsComments;
import com.sah.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsLikesRepository extends JpaRepository<CommentsLikes, Long> {
    long countByComment(PostsComments comment);
    boolean existsByCommentAndUser(PostsComments comment, Users user);
    @Transactional
    void deleteByCommentAndUser(PostsComments comment, Users user);
}
