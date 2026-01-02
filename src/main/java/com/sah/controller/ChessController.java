package com.sah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class ChessController {

    @GetMapping("/play")
    public String gamePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "play";
    }

    @GetMapping("/me")
    public String getUser(Principal principal) {
        return principal.getName();
    }
}