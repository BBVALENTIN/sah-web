package com.sah.dto.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private String username;
    private String content;
    private String userAvatar;
    private String createdAt;
    private long likeNumber;
    private long commentNumber;
    private boolean liked;
}
