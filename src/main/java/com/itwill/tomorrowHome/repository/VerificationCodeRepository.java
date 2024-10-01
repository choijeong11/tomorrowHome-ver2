package com.itwill.tomorrowHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tomorrowHome.domain.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

}
