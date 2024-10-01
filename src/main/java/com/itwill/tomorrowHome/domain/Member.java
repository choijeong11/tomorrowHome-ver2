package com.itwill.tomorrowHome.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.itwill.tomorrowHome.dto.MemberDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Member implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
	private String m_id;
	
	private String m_password;
	private String m_name;
	private String m_email;
	private String m_address;
	private String m_post;
	private String m_phone;
	private String m_verified; // 이메일 인증 여부

    @Builder
    public Member(String m_id, String m_password, String m_name, String m_email, String m_address, String m_post, String m_phone, String m_verified) {
		this.m_id = m_id;
		this.m_password = m_password;
		this.m_name = m_name;
		this.m_email = m_email;
		this.m_address = m_address;
		this.m_post = m_post;
		this.m_phone = m_phone;
		this.m_verified = m_verified;
	}
    
    // DTO를 엔티티로 변환하는 메서드
    public static Member fromDto(MemberDto dto) {
        return Member.builder()
                .m_id(dto.getM_id())
                .m_password(dto.getM_password())
                .m_name(dto.getM_name())
                .m_email(dto.getM_email())
                .m_address(dto.getM_address())
                .m_post(dto.getM_post())
                .m_phone(dto.getM_phone())
                .m_verified(dto.getM_verified())
                .build();
    }

    public void updatePw(String m_password) {
    	this.m_password = m_password;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return m_password;
	}

	@Override
	public String getUsername() {
		return m_id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Member [m_id=" + m_id + ", m_password=" + m_password + ", m_name=" + m_name + ", m_email=" + m_email
				+ ", m_address=" + m_address + ", m_post=" + m_post + ", m_phone=" + m_phone + "]";
	}
}
