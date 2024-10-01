package com.itwill.tomorrowHome.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/get_now_time", "/check_session", "/faq", "/product*", "/login*", "/member_register_rest"
                			, "/member_send_*", "/member_email_verification", "/member_find_*")
                .permitAll()  // 인증 없이 접근 가능한 경로
                .requestMatchers("/css/**", "/js/**", "/img/**", "/ckeditor/**", "/fonts/**", "/scss/**")
                .permitAll()  // 정적 자원에 대한 접근 허용
                .anyRequest().authenticated()  // 나머지 경로는 인증 필요
            )
            .formLogin((form) -> form
                .loginPage("/login_form")  // 커스텀 로그인 페이지 설정
                .loginProcessingUrl("/login")  // 로그인 처리 URL 설정
                .successHandler(customAuthenticationSuccessHandler())  // 성공 핸들러 등록
                .failureHandler(customAuthenticationFailureHandler())  // 실패 핸들러 등록
                .permitAll()
            )
            .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();  // 커스텀 성공 핸들러 빈 등록
    }
    
    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();  // 커스텀 실패 핸들러 빈 등록
    }
    
}
