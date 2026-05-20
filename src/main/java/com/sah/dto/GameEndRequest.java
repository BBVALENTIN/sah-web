package com.sah.dto;


import lombok.AllArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
public class GameEndRequest {
    public String lobbyId;
    public Principal principal;
}
