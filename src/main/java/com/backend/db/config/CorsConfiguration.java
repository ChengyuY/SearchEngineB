package com.backend.db.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT","OPTION")
                .maxAge(3600);
    }
}