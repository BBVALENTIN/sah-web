package com.sah.controller;

import com.sah.dto.requests.RegisterRequestDTO;
import com.sah.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {
    UsersService userService;

    public AccountController(UsersService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String showRegisterForm() { return "registration/register"; }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequestDTO req, Model model, HttpServletRequest httpRequest) {
        try {
            String ip = httpRequest.getHeader("X-Forwarded-For");
            if(ip == null) ip = httpRequest.getRemoteAddr();
            req.setIp(ip);
            userService.register(req);
            return "redirect:/success";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registration/register";
        }
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