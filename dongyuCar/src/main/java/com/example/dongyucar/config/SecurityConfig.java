package com.example.dongyucar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ⭐️ DELETE 요청 시 403 방지
                .cors(cors -> cors.disable())
                .headers(headers -> headers.frameOptions(f -> f.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**", "/reviews/**").permitAll() // ⭐️ 관리자/일반 경로 모두 허용
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
