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
    private final boolean active = false, deleted = true;
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

    private List<Posts> returnCleanPostsByLikes(Principal principal) {
        return postsRepository.findPostsByDeletedOrderByLikesNumberDesc(active)
                .stream().limit(limit).collect(Collectors.toList());
    }

//    private List<Posts> returnCleanPostsByDateNewestAndUser() {
//
//    }

    private List<Posts> returnCleanPostsByDateNewest() {
        return postsRepository.findPostsByDeletedOrderByCreatedAtDesc(active)
                .stream().limit(limit).toList();
    }

    public List<PostDTO> returnPosts(Principal principal) {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Posts> posts = returnCleanPostsByDateNewest();
        for(Posts post : posts) {
            PostDTO dto = returnPost(post.getPostId(), principal);
            postDTOs.add(dto);
        }
        return postDTOs;
    }

    public PostDTO returnPost(Long postId,Principal principal)
    {
        Posts post = postsRepository.findByPostId(postId);
        Users currentUser = checkUser(principal);

        return PostDTO.builder()
                .postId(postId)
                .liked(postsLikesRepository.existsByPostAndUser(post, currentUser))
                .commentNumber(getCommentNumber(post))
                .createdAt(post.getCreatedAt().format(formatter))
                .username(post.getCreator().getUsername())
                .userAvatar(post.getCreator().getAvatar())
                .likeNumber(postsLikesRepository.countByPost(post))
                .content(post.getContent())
                .build();
    }

    public void deletePost(Long postId, Principal principal) {
        Users currentUser = checkUser(principal);
        Posts post = postsRepository.findByPostId(postId);
        if(post.getCreator() != currentUser) {
            throw new RuntimeException("You didn't make the post, hence you can't delete it");
        }
        post.setDeleted(deleted);
        postsRepository.save(post);
    }

    public void deleteComment(Long commentId, Principal principal)
    {
        Users currentUser = checkUser(principal);
        PostsComments comment = postsCommentsRepository.findByCommentId(commentId);
        if(comment.getUser() != currentUser) {
            throw new RuntimeException("You didn't make the comment, hence you can't delete it");
        }
        comment.setDeleted(deleted);
        postsCommentsRepository.save(comment);
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
            post.setLikesNumber(post.getLikesNumber() - 1);
        }
        else {
            PostsLikes postsLikes = new PostsLikes(currentUser, post);
            postsLikesRepository.save(postsLikes);
            post.setLikesNumber(post.getLikesNumber() + 1);
        }
    }

    @Transactional
    public void likeComment(Long commentId, Principal principal)
    {
        Users currentUser = checkUser(principal);
        PostsComments comment = postsCommentsRepository.findByCommentId(commentId);
        if(commentsLikesRepository.existsByCommentAndUser(comment, currentUser))
        {
            commentsLikesRepository.deleteByCommentAndUser(comment, currentUser);
            comment.setLikesNumber(comment.getLikesNumber() - 1);
        }
        else {
            comment.setLikesNumber(comment.getLikesNumber() + 1);
            commentsLikesRepository.save(new CommentsLikes(comment, currentUser));
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
        List<PostsComments> comments = postsCommentsRepository.findByPostAndDeleted(post, active);
        List<CommentsDTO> commentsDTO = new ArrayList<>();

        for(PostsComments comment : comments) {
            CommentsDTO dto = CommentsDTO.builder()
                    .commentId(comment.getCommentId())
                    .createdAt(comment.getCreatedAt().format(formatter))
                    .likeNumber(commentsLikesRepository.countByComment(comment))
                    .liked(commentsLikesRepository.existsByCommentAndUser(comment, currentUser))
                    .creatorName(comment.getUser().getUsername())
                    .creatorAvatar(comment.getUser().getAvatar())
                    .content(comment.getContent())
                    .build();
            commentsDTO.add(dto);
        }
        return commentsDTO;
    }
}
