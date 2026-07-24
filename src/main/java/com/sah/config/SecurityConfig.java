package com.sah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("prod")
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.
                    csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/play/**", "/logout"))
                    .authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/register", "/css/**","/success", "/js/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form.loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .permitAll()
                    )
                    .logout((logout) -> logout.logoutSuccessUrl("/logout").permitAll());
            return http.build();
        }
}
