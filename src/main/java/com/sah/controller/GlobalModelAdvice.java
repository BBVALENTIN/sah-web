package com.sah.controller;

import com.sah.dto.loggedUser;
import com.sah.entity.Users;
import com.sah.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CustomUserDetailsService customUserDetailsService;

    public GlobalModelAdvice(CustomUserDetailsService customUserDetailsService)
    {
        this.customUserDetailsService = customUserDetailsService;
    }

    @ModelAttribute
    public void addUserData(Model model, HttpServletRequest request, Principal principal) {

        String uri = request.getRequestURI();

        // EXCLUDE pagini
        if (uri.startsWith("/login") ||
                uri.startsWith("/register") ||
                uri.startsWith("/public") ||
                uri.startsWith("/error")) {
            return;
        }

        if (principal != null) {
            loggedUser user = customUserDetailsService.loadInfo(principal);

            model.addAttribute("avatar", user.getAvatar());
            model.addAttribute("username", user.getUsername());
        }
    }
}
