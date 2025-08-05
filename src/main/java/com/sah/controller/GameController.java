package com.sah.controller;

import com.sah.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class GameController {

    @GetMapping("/game")
    public String gamePage(Model model, Principal principal) {
        if(principal != null)
            model.addAttribute("username", principal.getName());
        else
            model.addAttribute("username", "admin");

        return "game";
    }

}
