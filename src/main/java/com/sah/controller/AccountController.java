package com.sah.controller;

import com.sah.dto.RegisterRequestDTO;
import com.sah.service.UsersService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Profile("prod")
public class AccountController {
    UsersService userService;

    public AccountController(UsersService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String showRegisterForm() { return "registration/register"; }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequestDTO request) {
        userService.register(request);
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String showSuccessStatus() { return "registration/success"; }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Numele de utilizator sau parola sunt incorecte.");
        }
        return "registration/login";
    }
}