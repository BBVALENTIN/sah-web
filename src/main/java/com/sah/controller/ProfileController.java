package com.sah.controller;

import com.sah.dto.MatchHistoryDTO;
import com.sah.service.ProfileService;
import jakarta.persistence.Column;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public String getProfile(@PathVariable String username, Model model) {
        List<MatchHistoryDTO> matchHistory = profileService.loadProfileGames(username);
        model.addAttribute("matchHistory", matchHistory);

        return "profile/index";
    }

}
