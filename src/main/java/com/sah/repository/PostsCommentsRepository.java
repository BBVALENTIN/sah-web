package com.sah.repository;

import com.sah.entity.Posts;
import com.sah.entity.PostsComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsCommentsRepository extends JpaRepository<PostsComments, Long> {
    long countByPost(Posts post);
}
