package com.itwill.tomorrowHome.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberDto {
	private String m_id;
	private String m_password;
	private String m_name;
	private String m_email;
	private String m_address;
	private String m_post;
	private String m_phone;
	private String m_verified;
	private String new_password;
	private String verification_code; // 입력한 인증코드 값

    public MemberDto(String m_id, String m_password, String m_name, String m_email, String m_address, String m_post, String m_phone, String m_verified, String new_password, String verification_code) {
		this.m_id = m_id;
		this.m_password = m_password;
		this.m_name = m_name;
		this.m_email = m_email;
		this.m_address = m_address;
		this.m_post = m_post;
		this.m_phone = m_phone;
		this.m_verified = m_verified;
		this.new_password = new_password;
		this.verification_code = verification_code;
	}

}
