package com.sah.controller.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsPageController {
    @GetMapping("/user")
    public String showUserSettings() {
        return "settings/usersettings";
    }

    @GetMapping("/admin")
    public String showAdminSettings() {
        return "settings/adminsettings";
    }
}
