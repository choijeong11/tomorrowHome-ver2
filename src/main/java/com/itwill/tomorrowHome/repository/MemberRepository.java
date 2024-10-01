package com.itwill.tomorrowHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    /**
    메일 등록 여부 조회
    */
	@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.m_email = :email")
    boolean existsByEmail(@Param("email") String email);
	
	/**
    메일로 회원 조회
	*/
	@Query("SELECT m FROM Member m WHERE m.m_email = :email")
	Member findByEmail(@Param("email") String email);
}
