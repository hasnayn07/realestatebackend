package com.realestatebackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${security.cors.allowed-origins:http://localhost:3000}") private String allowed;
    @Override public void addCorsMappings(CorsRegistry r){
        r.addMapping("/**").allowedOrigins(allowed.split(","))
                .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                .allowedHeaders("*").allowCredentials(true);
    }
}