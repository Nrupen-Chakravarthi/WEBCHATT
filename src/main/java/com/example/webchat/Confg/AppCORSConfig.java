//package com.example.webchat.Confg;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class AppCORSConfig {
//
//    /**
//     * Configures global CORS rules for REST endpoints.
//     */
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // Apply CORS configuration to all API paths
//
//                        // 1. Allow the specific origin of your React frontend
//                        .allowedOrigins("http://localhost:3000")
//
//                        // 2. Allow all necessary HTTP methods (crucial for preflight OPTIONS)
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//
//                        // 3. Allow all headers, including the Authorization header (JWT)
//                        .allowedHeaders("*")
//
//                        // 4. Important for passing credentials/cookies (though JWT is stateless)
//                        .allowCredentials(true);
//            }
//        };
//    }
//}