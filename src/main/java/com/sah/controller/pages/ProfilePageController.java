package com.sah.controller.pages;

import com.sah.dto.misc.ErrorResponseDTO;
import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfilePageController {

    private final ProfileService profileService;

    public ProfilePageController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public String getProfile(@PathVariable String username, Model model, Principal principal) {
        ProfileInfoDTO profileInfoDTO = profileService.loadProfileInfo(username);
        if(profileInfoDTO == null) {
            model.addAttribute("error", new ErrorResponseDTO());
            return "errors/errorpage";
        }
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("userInfo", profileInfoDTO.getUserInfo());
        model.addAttribute("gameInfo", profileInfoDTO.getGameInfo());

        return "profile/index";
    }

}
