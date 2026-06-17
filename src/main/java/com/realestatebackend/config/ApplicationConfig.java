package com.realestatebackend.config;

import com.realestatebackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration @RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepo;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepo.findByEmail(username)
                .map(u -> org.springframework.security.core.userdetails.User.builder()
                        .username(u.getEmail())
                        .password(u.getPassword())
                        .roles(u.getRole().name())
                        .disabled(!u.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}