package com.sah.controller.pages;

import com.sah.controller.ProfileController;
import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.service.PostsService;
import com.sah.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CommunityPageController {

    private ProfileService profileService;

    public CommunityPageController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/community")
    public String showCommunityPage(Model model, Principal principal) {
        ProfileInfoDTO profileInfoDTO = profileService.loadProfileInfo(principal.getName());
        model.addAttribute("username", profileInfoDTO.getUsername());
        model.addAttribute("useravatar", profileInfoDTO.getAvatar());
        return "community/community";
    }

}
