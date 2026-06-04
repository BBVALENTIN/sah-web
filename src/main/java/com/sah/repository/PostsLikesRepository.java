package com.sah.repository;

import com.sah.entity.Posts;
import com.sah.entity.PostsLikes;
import com.sah.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsLikesRepository extends JpaRepository<PostsLikes, Long> {
    boolean existsByPostAndUser(Posts post, Users user);
    @Transactional
    void deleteByPostAndUser(Posts post, Users user);
    long countByPost(Posts post);
}
