package com.sah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PracticeController {
    @GetMapping("/selfpractice")
    public String showSelfPractice() {
        return "practice/selfpractice";
    }
}
