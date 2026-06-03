package com.sah.repository;

import com.sah.entity.Posts;
import com.sah.entity.PostsLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsLikesRepository extends JpaRepository<PostsLikes, Long> {
    long countByPost(Posts post);
}
