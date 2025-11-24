package com.example.webchat.Filter;

import com.example.webchat.Service.CustomUserDetailsService;
import com.example.webchat.Service.JwtService;
import com.example.webchat.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService service;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Get Authorization Header
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println(jwt);

            try {
                username = service.extractUsername(jwt);
            } catch (Exception e) {
                // Log and absorb exceptions like expired token, corrupted JWT, etc.
                // This prevents the filter chain from crashing and ensures the 403 is delivered.
                System.err.println("JWT extraction failed: " + e.getMessage());
            }
        }

        // 2. Check if username exists and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            System.out.println(userDetails.getUsername());

            // Validate the token
            if (service.validateToken(jwt, userDetails)) {

                // --- SUCCESS PATH ---
                System.out.println("JWT successfully validated for user: " + username);

                userService.updateLastSeen(username);


                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } else {
                // --- FAILURE PATH ---
                // This log is CRITICAL for diagnosing the 403
                System.err.println("JWT Validation FAILED for user: " + username + ". Token is invalid or expired.");
            }
        }

        // 3. Continue filter chain
        // If authentication failed in step 2, the request is marked unauthenticated and
        // the default Spring Security filters will return the 403 Forbidden later.
        filterChain.doFilter(request, response);
    }
}