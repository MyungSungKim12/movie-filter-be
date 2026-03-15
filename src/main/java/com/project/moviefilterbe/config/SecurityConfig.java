package com.project.moviefilterbe.config;

import com.project.moviefilterbe.login.handler.OAuth2SuccessHandler; // 추가
import com.project.moviefilterbe.login.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // 추가
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // 추가
    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilter(corsFilter) // CORS 처리
                // 1. JWT 사용을 위해 세션 설정을 STATELESS로 변경
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/**").permitAll() // TestController
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/login-success/**").permitAll()
                        .requestMatchers("/api/movie/**", "/api/user/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 2. defaultSuccessUrl 대신 핸들러를 등록
                        .successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }

}