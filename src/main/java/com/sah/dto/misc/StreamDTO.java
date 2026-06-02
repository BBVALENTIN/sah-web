package com.sah.dto.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StreamDTO {
    private String thumbnailUrl;
    private String userName;
    private String title;
    private int viewerCount;
}
