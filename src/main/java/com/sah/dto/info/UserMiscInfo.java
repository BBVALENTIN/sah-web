package com.sah.dto.info;

import lombok.*;

@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class UserMiscInfo {
    private String username;
    private String avatar;
}
