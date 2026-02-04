package com.example.dongyucar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보안 비활성화 (API 서버에서는 보통 끕니다)
                .csrf(csrf -> csrf.disable())

                // 2. 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 경로들은 로그인 없이 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 나머지 모든 요청은 로그인이 필요함
                        .anyRequest().authenticated()
                )

                // 3. 기본 로그인 폼 유지 (원치 않으시면 제거 가능)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
