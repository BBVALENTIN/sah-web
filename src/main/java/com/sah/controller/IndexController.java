package com.sah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String showIndex(Model model, Principal principal)
    {
        model.addAttribute("username", principal.getName());
        return "index";
    }
}
