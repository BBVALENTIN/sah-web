package com.sah.service;

import com.sah.dto.requests.PostRequestDTO;
import com.sah.entity.Posts;
import com.sah.entity.Users;
import com.sah.repository.PostsRepository;
import com.sah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PostsService {
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository, UserRepository userRepository)
    {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
    }

    public Posts savePost(PostRequestDTO dto, Principal principal) {
        Users currentUser = userRepository.findByUsername(principal.getName());
        if(currentUser == null) throw new RuntimeException("Current user is null! You can't post unless you're logged in");

        Posts posts = new Posts(dto.getContent(), currentUser);
        return postsRepository.save(posts);
    }
}
