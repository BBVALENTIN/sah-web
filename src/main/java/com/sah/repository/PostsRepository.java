package com.sah.repository;

import com.sah.entity.Posts;
import com.sah.entity.PostsComments;
import com.sah.entity.PostsLikes;
import com.sah.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    List<Posts> findPostsByDeleted(boolean deleted);

    Posts findByPostId(Long postId);
    Page<Posts> findByDeletedOrderByCreatedAtDesc(boolean deleted, Pageable pageable);
    List<Posts> findPostsByDeletedAndCreatorNot(boolean deleted, Users creator);
    List<Posts> findPostsByDeletedAndCreatorNotOrderByCreatedAtDesc(boolean deleted, Users creator);
    List<Posts> findPostsByDeletedOrderByLikesNumberDesc(boolean deleted);
    List<Posts> findPostsByDeletedOrderByCreatedAtDesc(boolean deleted);
}

