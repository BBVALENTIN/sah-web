package com.sah.controller.api;

import com.sah.dto.info.DescriptionDTO;
import com.sah.service.ProfileService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/settings")
public class SettingsApiController {

    private final ProfileService profileService;

    public SettingsApiController(ProfileService profileService)
    {
        this.profileService = profileService;
    }

    @PutMapping("/changeDescription")
    public String changeDescription(@RequestBody DescriptionDTO req, Principal principal) {
        return profileService.changeDescription(req.getDescription(), principal);
    }

    @PutMapping("/changeCountry")
    public ResponseEntity<?> changeCountry(@RequestParam String countryISOCode, Principal principal) {
        profileService.changeCountry(countryISOCode, principal);
        return ResponseEntity.ok().build();
    }
}
