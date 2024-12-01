package com.app.ticket_booking_simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Match your API path
                        .allowedOrigins("http://localhost:3000") // React app origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
