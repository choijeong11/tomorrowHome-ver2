package com.itwill.tomorrowHome.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	@Autowired
    private JavaMailSender mailSender;
	
	/**
	 * 이메일 전송
	 */
	@Transactional
	public void sendEmail(String email, String subject, String message) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(message, true); // true로 설정하여 HTML 지원
        
        mailSender.send(mimeMessage);
    }
	
}
