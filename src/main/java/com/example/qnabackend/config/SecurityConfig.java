package com.example.qnabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                        // 공개 GET
                        .requestMatchers(HttpMethod.GET, "/api/qna/questions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/qna/answers/**").permitAll()
                        // 개발단계 임시 전체 허용 (POST/PUT/DELETE/PATCH)
                        .requestMatchers(HttpMethod.POST,   "/api/qna/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/api/qna/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/qna/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH,  "/api/qna/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
