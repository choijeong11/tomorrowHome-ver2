package com.itwill.tomorrowHome.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "verification_code")
public class VerificationCode {
    @Id
    private String email;
    
    private String code;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;
    
    @Builder
	public VerificationCode(String email, String code, LocalDateTime createdDate, LocalDateTime expiryDate) {
    	this.email = email;
		this.code = code;
		this.createdDate = createdDate;
		this.expiryDate = expiryDate;
	}

    // 코드 만료 여부
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

}
