package com.sah.controller;

import com.sah.dto.MatchHistoryDTO;
import com.sah.dto.ProfileInfoDTO;
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
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public String getProfile(@PathVariable String username, Model model) {
        ProfileInfoDTO profileInfoDTO = profileService.loadProfileInfo(username);
        if(profileInfoDTO.getUsername() == null) {
            return "profile/notfound";
        }
        model.addAttribute("username", profileInfoDTO.getUsername());
        model.addAttribute("matchHistory", profileInfoDTO.getMatchHistoryDTOList());

        return "profile/index";
    }

}
