package com.shake_art.back.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              // Toutes les routes de ton API
                .allowedOrigins("*")            // Toutes origines (à restreindre plus tard)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                .allowedHeaders("*")            // Tous les headers acceptés
                .allowCredentials(false);       // Sans cookies ni credentials (sinon, préciser l'origine)
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🔥 Permet d'accéder à /uploads/photos/** via HTTP
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

}
