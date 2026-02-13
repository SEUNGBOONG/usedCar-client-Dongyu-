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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                // ⭐️ 추가: HTTPS 관련 보안 헤더 설정
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // H2 콘솔 등을 위해
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/reviews/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
