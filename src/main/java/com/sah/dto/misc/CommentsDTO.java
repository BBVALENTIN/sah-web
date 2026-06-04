package com.sah.dto.misc;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsDTO {
    private Long commentId;
    private String creatorName;
    private String creatorAvatar;
    private String content;
    private long likeNumber;
    private boolean liked;
    private String createdAt;
}
