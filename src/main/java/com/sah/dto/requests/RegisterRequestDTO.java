package com.sah.dto.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private String ip;
}
