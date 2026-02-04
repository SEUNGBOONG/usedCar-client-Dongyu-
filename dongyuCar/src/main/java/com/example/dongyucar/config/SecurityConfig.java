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
                .csrf(csrf -> csrf.disable()) // CSRF 끄기
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ⭐️ 핵심: 모든 요청(Swagger 포함) 로그인 없이 허용!
                );
        return http.build();
    }
}
