package com.sah.controller.pages;

import com.sah.dto.chess.MinimalStateDTO;
import com.sah.service.PracticeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PracticePageController {
    PracticeService practiceService;

    PracticePageController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping("/selfpractice")
    public String showSelfPractice() {
        return "practice/selfpractice";
    }

    @GetMapping("/playengine")
    public String showEnginePage() {
        return "practice/playengine";
    }

    @GetMapping("/practiceBoard")
    @ResponseBody
    public MinimalStateDTO getPracticeBoard() {
        return practiceService.initialize();
    }
}
