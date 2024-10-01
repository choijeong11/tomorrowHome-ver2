package com.itwill.tomorrowHome.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.VerificationCode;
import com.itwill.tomorrowHome.repository.VerificationCodeRepository;

@Service
public class AuthService {
	@Autowired
	private VerificationCodeRepository verificationCodeRepository;
	
	/**
	 * 인증 코드 확인
	 */
	public int verifyCode(String inputCode, String email) throws Exception {
		int result = 0;
		VerificationCode verificationCode = verificationCodeRepository.findById(email)
											.orElseThrow(() -> new UsernameNotFoundException(""));
		if (verificationCode != null) {
			if(verificationCode.getCode().equals(inputCode)) {
				result = 1; // 인증 완료
				if(verificationCode.isExpired()) {
					result = -1; // 기한 만료
				}
			}else {
				result = -2; // 코드 불일치
			}
		} 
		
		return result;
	}
	
	/**
	 * 인증 코드 정보 DB 저장
	 */
	@Transactional
	public void saveVerificationCode(String code, String email) throws Exception {
        VerificationCode verificationCode 
	        = VerificationCode.builder()
	        .email(email)
	        .code(code)
	        .expiryDate(LocalDateTime.now().plusMinutes(5)) // 5분 후 만료
	        .createdDate(LocalDateTime.now())
	        .build();
	        verificationCodeRepository.save(verificationCode); 
    }
	
}
