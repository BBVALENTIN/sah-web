package com.sah.controller.pages;

import com.sah.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdviceController {

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if(customUserDetails != null)
        {
            model.addAttribute("username", customUserDetails.getUsername());
            model.addAttribute("avatar", customUserDetails.getAvatarUrl());
        }
    }
}
