package com.example.webchat.Confg;

import com.example.webchat.Filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
// ... (all other imports)

@Configuration
public class SecurityConfig {

    // Removed the field injection, which is correct.

    @Bean
    // 🎯 CRITICAL FIX: Inject JwtFilter as a method parameter (METHOD INJECTION)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        // ^ The correct variable name is now 'jwtFilter' (lowercase 'j')

        // 1. Disable CSRF (Stateless application)
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. Configure CORS
        http.cors(c -> c.configurationSource(corsConfigurationSource()));

        // 3. Define Request Authorization rules
        http.authorizeHttpRequests(c -> c
                .requestMatchers(
                        "/api/register",
                        "/api/login",
                        "/api/verifyOTP",
                        "/ws/**"
                ).permitAll()
                .anyRequest().authenticated());

        // 4. Set Session Management to Stateless (JWT mode)
        http.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 5. Add the JWT filter before the standard authentication process
        // 🎯 FIX: Use the injected parameter: 'jwtFilter'
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- CRITICAL FIX: Robust CORS Configuration Source Bean ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ... (implementation is correct) ...
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration manager) throws Exception {
        return manager.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}