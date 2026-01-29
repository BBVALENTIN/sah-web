package com.sah.controller;

import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Profile("prod")
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/register")
    public String showRegisterForm()
    {
        return "registration/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public String handleRegister(@RequestParam String username, @RequestParam String password)
    {
        if(userRepository.findByUsername(username) != null)
            return "Username folosit deja, alege altul";

        String cryptedPassword = encoder.encode(password);

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(cryptedPassword);

        userRepository.save(user);
        return "User adaugat cu succes";
    }
}
