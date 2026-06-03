package com.sah.service;

import com.sah.dto.misc.PostDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.entity.Posts;
import com.sah.entity.PostsLikes;
import com.sah.entity.Users;
import com.sah.repository.PostsLikesRepository;
import com.sah.repository.PostsRepository;
import com.sah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostsService {
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final PostsLikesRepository postsLikesRepository;
    private int limit = 30;
    private boolean activePost = false, deletedPost = true;
    @Autowired
    public PostsService(PostsRepository postsRepository, UserRepository userRepository, PostsLikesRepository postsLikesRepository)
    {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.postsLikesRepository = postsLikesRepository;
    }

    public Posts savePost(PostRequestDTO dto, Principal principal) {
        Users currentUser = checkUser(principal);

        Posts posts = new Posts(dto.getContent(), currentUser);
        return postsRepository.save(posts);
    }

    private List<Posts> returnCleanPosts(Principal principal) {
        Users currentUser = checkUser(principal);
        return postsRepository.findPostsByDeletedAndCreatorNot(activePost, currentUser).stream().limit(limit).collect(Collectors.toList());
    }

    public List<PostDTO> returnPosts(Principal principal) {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Posts> posts = returnCleanPosts(principal);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(Posts post : posts) {
            Users users = post.getCreator();
            PostDTO postDTO = new PostDTO(users.getUsername(), post.getContent(), users.getAvatar(), post.getCreatedAt().format(formatter), getLikeNumber(post));
            postDTOs.add(postDTO);
        }

        return postDTOs;
    }

    public void deletePost(Long postId) {
        Posts post = postsRepository.findByPostId(postId);
        post.setDeleted(deletedPost);
        postsRepository.save(post);
    }

    private Users checkUser(Principal principal){
        Users currentUser = userRepository.findByUsername(principal.getName());
        if(currentUser == null) throw new RuntimeException("Current user is null! You can't post unless you're logged in");

        return currentUser;
    }

    public void likePost(Long postId, Principal principal) {
        Users currentUser = checkUser(principal);
        Posts post = postsRepository.findByPostId(postId);
        PostsLikes postsLikes = new PostsLikes(currentUser, post);
        postsLikesRepository.save(postsLikes);
    }

    private long getLikeNumber(Posts post) {
        return postsLikesRepository.countByPost(post);
    }

    public void publishComment(Long postId, Principal principal)
    {
        Users currentUser = checkUser(principal);

    }
}
