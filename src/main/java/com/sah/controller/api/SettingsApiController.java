package com.sah.controller.api;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsApiController {

    @PutMapping("/changeDescription")
    public void changeDescription() {

    }

    @PutMapping("/changeCountry")
    public void changeCountry() {

    }
}
