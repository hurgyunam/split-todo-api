package com.overtheinfinite.splittodo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 🔑 모든 경로(endpoint)에 CORS 허용
                .allowedOrigins("http://localhost:3000") // 🔑 Your frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed methods
                .allowedHeaders("*") // Allows all headers, including Content-Type
                .allowCredentials(true) // If you use cookies or session IDs
                .maxAge(3600); // Cache preflight response for 1 hour

        // Alternatively, to allow all paths for simplicity during development:
        // registry.addMapping("/**")
        //         .allowedOrigins("http://localhost:3000")
        //         // ... rest of the settings
    }
}
