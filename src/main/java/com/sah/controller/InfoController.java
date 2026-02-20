package com.sah.controller;

import com.sah.dto.lobbyDTO;
import com.sah.dto.loggedUser;
import com.sah.service.CustomUserDetailsService;
import com.sah.service.UsersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/info")
public class InfoController {

    CustomUserDetailsService customUserDetailsService;

    public InfoController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/user")
    public lobbyDTO getLoggedUser(Principal principal) {
        lobbyDTO currentLobby = new lobbyDTO();
        if(principal == null) {
            throw new RuntimeException("Principal is null");
        }
        currentLobby.setLoggedUsername(customUserDetailsService.loadInfo(principal));

        return currentLobby;
    }
}
