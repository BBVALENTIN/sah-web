package com.sah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {
    @GetMapping("/vsEngine")
    public String showEnginePage() {
        return "practice/playBot";
    }
}
