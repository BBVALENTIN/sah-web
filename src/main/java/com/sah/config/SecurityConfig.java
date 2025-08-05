package com.sah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.
                    authorizeHttpRequests(auth -> auth.requestMatchers("/login", "register", "/css/**","/game", "/js/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form.loginPage("/login")
                            .defaultSuccessUrl("/index", true)
                            .permitAll()
                    )
                    .logout(logout -> logout.permitAll());
            return http.build();
        }
}
