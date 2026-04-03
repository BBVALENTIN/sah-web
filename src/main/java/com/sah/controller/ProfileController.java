package com.sah.controller;

import jakarta.persistence.Column;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class ProfileController {

    @GetMapping("/{loggedUser}")
    public String getYourProfile(@PathVariable String loggedUser) {
       return "profile/index";
    }
}
