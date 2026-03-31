package com.sah.controller;

import com.sah.dto.MinimalStateDTO;
import com.sah.service.PracticeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PracticeController {
    PracticeService practiceService;

    PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping("/selfpractice")
    public String showSelfPractice() {
        return "practice/selfpractice";
    }

    @GetMapping("/practiceBoard")
    @ResponseBody
    public MinimalStateDTO getPracticeBoard() {
        return practiceService.initialize();
    }
}
