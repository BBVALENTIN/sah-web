package com.sah.service;

import com.sah.dto.misc.CommentsDTO;
import com.sah.dto.misc.PostDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.entity.*;
import com.sah.repository.*;
import jakarta.transaction.Transactional;
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
    private final PostsCommentsRepository postsCommentsRepository;
    private final CommentsLikesRepository commentsLikesRepository;
    private final int limit = 30;
    private final boolean activePost = false, deletedPost = true;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public PostsService(
            PostsRepository postsRepository,
            UserRepository userRepository,
            PostsLikesRepository postsLikesRepository,
            PostsCommentsRepository postsCommentsRepository,
            CommentsLikesRepository commentsLikesRepository)
    {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.postsLikesRepository = postsLikesRepository;
        this.postsCommentsRepository = postsCommentsRepository;
        this.commentsLikesRepository = commentsLikesRepository;
    }

    public Posts savePost(PostRequestDTO dto, Principal principal) {
        Users currentUser = checkUser(principal);

        Posts posts = new Posts(dto.getContent(), currentUser);
        return postsRepository.save(posts);
    }

    private List<Posts> returnCleanPosts(Principal principal) {
        Users currentUser = checkUser(principal);
        return postsRepository.findPostsByDeletedAndCreatorNotOrderByCreatedAtDesc(activePost, currentUser)
                .stream().limit(limit).collect(Collectors.toList());
    }

    public List<PostDTO> returnPosts(Principal principal) {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Posts> posts = returnCleanPosts(principal);
        for(Posts post : posts) {
            Users users = post.getCreator();
            PostDTO dto = returnPost(post.getPostId(), principal);
            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public PostDTO returnPost(Long postId,Principal principal)
    {
        Posts post = postsRepository.findByPostId(postId);
        Users currentUser = checkUser(principal);
        PostDTO postDTO = PostDTO.builder()
                .postId(postId)
                .liked(postsLikesRepository.existsByPostAndUser(post, currentUser))
                .commentNumber(getCommentNumber(post))
                .createdAt(post.getCreatedAt().format(formatter))
                .username(currentUser.getUsername())
                .userAvatar(currentUser.getAvatar())
                .likeNumber(postsLikesRepository.countByPost(post))
                .content(post.getContent())
                .build();

        return postDTO;
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

    @Transactional
    public void likePost(Long postId, Principal principal) {
        Users currentUser = checkUser(principal);
        Posts post = postsRepository.findByPostId(postId);
        if(postsLikesRepository.existsByPostAndUser(post, currentUser))
        {
            postsLikesRepository.deleteByPostAndUser(post, currentUser);
        }
        else {
            PostsLikes postsLikes = new PostsLikes(currentUser, post);
            postsLikesRepository.save(postsLikes);
        }
    }

    private long getLikeNumber(Posts post) {
        return postsLikesRepository.countByPost(post);
    }
    private long getCommentNumber(Posts post) { return postsCommentsRepository.countByPost(post); }

    public void publishComment(Long postId,String content, Principal principal)
    {
        Users currentUser = checkUser(principal);
        Posts post = postsRepository.findByPostId(postId);
        PostsComments comment = new PostsComments(currentUser, post, content);
        postsCommentsRepository.save(comment);
    }

    public List<CommentsDTO> getComments(Long postId, Principal principal)
    {
        Users currentUser = checkUser(principal);
        Posts post = postsRepository.findByPostId(postId);
        List<PostsComments> comments = postsCommentsRepository.findByPost(post);
        List<CommentsDTO> commentsDTO = new ArrayList<>();

        for(PostsComments comment : comments) {
            Users creator = userRepository.findByUsername(comment.getUser().getUsername());
            CommentsDTO dto = CommentsDTO.builder()
                    .commentId(comment.getCommentId())
                    .createdAt(comment.getCreatedAt().format(formatter))
                    .likeNumber(commentsLikesRepository.countByComment(comment))
                    .liked(commentsLikesRepository.existsByCommentAndUser(comment, currentUser))
                    .creatorName(comment.getUser().getUsername())
                    .creatorAvatar(creator.getAvatar())
                    .build();
            commentsDTO.add(dto);
        }
        return commentsDTO;
    }
}
