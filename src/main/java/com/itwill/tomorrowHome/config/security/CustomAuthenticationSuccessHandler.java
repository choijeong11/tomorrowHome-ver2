package com.itwill.tomorrowHome.config.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.itwill.tomorrowHome.domain.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

    	// 로그인 회원 정보 세션에 담기
        Object principal = authentication.getPrincipal();
        Member sMember = (Member) principal;
    	String m_id = sMember.getM_id();
        if (principal instanceof UserDetails) {
            HttpSession session = request.getSession();
            session.setAttribute("sMember", sMember);  
            session.setAttribute("sM_id", m_id);  
        }

        // 인증 성공 후의 리다이렉션 처리
        response.sendRedirect("/index"); 
    }
}
