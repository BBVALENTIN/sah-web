package com.sah.dto.misc;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
