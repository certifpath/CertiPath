package com.appAuditza.Auditza.Backend.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                // On autorise l'accès anonyme à toutes les pages et ressources publiques.
                // La protection de ces pages sera gérée par le script guard.js côté client.
                .requestMatchers(
                    // Endpoints d'API publics
                    "/api/auth/**", 
                    "/api/images/**",
                    
                    // Pages et ressources statiques
                    "/", 
                    "/index.html", 
                    "/login.html", 
                    "/signup.html", 
                    "/qrcode.html", 
                    "/verify-otp.html",
                    
                    // --- AJOUTS CRUCIAUX ICI ---
                    "/home.html",
                    "/dashbordrssi.html",
                    "/admin.html",
                    // --- FIN DES AJOUTS ---
                    
                    "/css/**", 
                    "/js/**", 
                    "/favicon.ico"
                ).permitAll()
                
                // Toutes les autres requêtes (en particulier les autres endpoints d'API)
                // nécessitent une authentification.
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}