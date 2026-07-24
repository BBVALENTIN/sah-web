package com.sah.dto.requests;


import com.sah.entity.Users;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameEndRequest {
    public String lobbyId;
    public Users currentUser;
}
