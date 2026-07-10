package com.sah.controller.pages;

import com.sah.dto.requests.RegisterRequestDTO;
import com.sah.service.user.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthPageController {

    private final UsersService usersService;

    public AuthPageController(UsersService usersService)
    {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequestDTO req, Model model, HttpServletRequest httpRequest) {
        try {
            String ip = httpRequest.getHeader("X-Forwarded-For");
            if(ip == null) ip = httpRequest.getRemoteAddr();
            req.setIp(ip);
            usersService.register(req);
            return "redirect:/success";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "registration/register";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() { return "registration/register"; }

    @GetMapping("/success")
    public String showSuccessStatus() { return "registration/success"; }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Username or password are incorrect.");
        }
        return "registration/login";
    }

}
