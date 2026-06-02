package com.sah.dto.misc;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class loggedUser {
    public Long userId;
    public String username;
    public String avatar;
}
