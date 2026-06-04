package com.sah.repository;

import com.sah.entity.Posts;
import com.sah.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    List<Posts> findPostsByDeleted(boolean deleted);

    Posts findByPostId(Long postId);

    List<Posts> findPostsByDeletedAndCreatorNot(boolean deleted, Users creator);
    List<Posts> findPostsByDeletedAndCreatorNotOrderByCreatedAtDesc(boolean deleted, Users creator);
}
