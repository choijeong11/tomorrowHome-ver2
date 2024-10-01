package com.itwill.tomorrowHome.service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Member;
import com.itwill.tomorrowHome.dto.MemberDto;
import com.itwill.tomorrowHome.repository.MemberRepository;

@Service
public class MemberService implements UserDetailsService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MailService mailService;
	@Autowired
	private AuthService authService;
	
	/**
	 * 회원가입 
	 */
	@Transactional
	public int createMember(MemberDto memberDto) throws Exception {
		// 비밀번호 암호화
		memberDto.setM_password(passwordEncoder.encode(memberDto.getM_password()));
		Member member = Member.fromDto(memberDto); 
				
		if(memberRepository.existsById(member.getM_id())) return -1; // 아이디 중복
		if(memberRepository.existsByEmail(member.getM_email())) return -2; // 이메일 중복
		
		// 이메일 인증 확인 
		String inputVerificationCode = memberDto.getVerification_code();
		int verifyResult = authService.verifyCode(inputVerificationCode, memberDto.getM_email());
		if(verifyResult != 1 && verifyResult != -1) {
			return -3; // 이메일 미인증
		}
		
		// 회원등록
		memberRepository.save(member);
		return 1;
	}
	
	/**
	 * 회원 로그인
	 */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String m_id) throws UsernameNotFoundException {
		return memberRepository.findById(m_id).orElseThrow(() -> new UsernameNotFoundException(""));
    }
    
	/**
	 * 회원 정보 수정
	 */
	@Transactional
	public Member updateMember(MemberDto memberDto) throws Exception {
		// 비밀번호 암호화
		memberDto.setM_password(passwordEncoder.encode(memberDto.getM_password()));
		Member member = Member.fromDto(memberDto); 
		// 비밀변호 변경 시 
		if(memberDto.getNew_password() != null && !memberDto.getNew_password().equals("")) {
			String newPw = passwordEncoder.encode(memberDto.getNew_password());
			member.updatePw(newPw);
		}
		// 수정
		return memberRepository.save(member);
	}
	
	/**
	 * 회원 탈퇴
	 */
	@Transactional
	public void deleteMember(MemberDto memberDto) throws Exception {
		memberRepository.deleteById(memberDto.getM_id());
	}
	
	/**
	 * 회원 정보 찾기(ID)
	 */
	@Transactional(readOnly = true)
	public Member findMember(String m_id) throws Exception {
		return memberRepository.findById(m_id).orElseThrow(() -> new UsernameNotFoundException(""));
	}
	
	/**
	 * 회원 정보 찾기(email)
	 */
	@Transactional(readOnly = true)
	public Member findMemberByEmail(String m_email) throws Exception {
		return memberRepository.findByEmail(m_email);
	}
	
	/**
	 * 회원 인증 이메일 전송
	 */
	@Transactional
	public void sendMemberVerificationEmail(String email) throws Exception {
		 String code = String.format("%06d", new Random().nextInt(999999));  // 6자리 숫자 코드 생성
        
        // 이메일 전송 내용
        String subject = "내일의집 이메일 인증 코드 요청";
        String message = "<p>안녕하세요, 내일의집 입니다 😊</p><br/>"
                + "<p>회원 가입을 완료하시려면 아래 인증 코드를 입력하세요.</p><br/>"
                + "<p>인증 코드는 5분간 유효합니다.</p><br/>"
                + "<p>감사합니다.</p><br/>"
                + "<p>* 인증 코드 : <strong>" + code + "</strong></p><br/>";

        // 메일 전송
        mailService.sendEmail(email, subject, message);
        // 인증코드 정보 DB 저장
        authService.saveVerificationCode(code, email);
    }
	
	/**
	 * 아이디/비밀번호 찾기 이메일 전송
	 */
	@Transactional
	public int sendMemberFindEmail(String type, String email) throws Exception {
		String subject = "";
		String message = "";
		
		// 회원 조회
		Member findMember = memberRepository.findByEmail(email);
		if(findMember == null) {
			return -1; // 회원 없음
		}
		
		if("id".equals(type)) {
			subject = "내일의집 회원 아이디 찾기 이메일 요청";
			message = "<p>안녕하세요, 내일의집 입니다 😊</p><br/>"
					+ "<p>요청하신 아이디는 [ " + findMember.getM_id() + " ] 입니다.</p><br/>"
					+ "<p>감사합니다.</p>";
			
		}else if("pw".equals(type)) {
			// 임시 비밀번호 발급
			String tempPw = UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "").substring(0, 6);
			// 비밀번호 암호화
			String encryptionTempPw = passwordEncoder.encode(tempPw);
			// 발급된 임시 비밀번호로 변경
			findMember.updatePw(encryptionTempPw);
			memberRepository.save(findMember);
			
			subject = "내일의집 회원 비밀번호 찾기 이메일 요청";
			message = "<p>안녕하세요, 내일의집 입니다 😊</p><br/>"
					+ "<p>임시 비밀번호가 발급되었습니다.</p><br/>"
					+ "<p>아래 임시 비밀번호를 입력하여 새로운 비밀번호를 설정하세요.</p><br/>"
					+ "<p>감사합니다.</p><br/>"
					+ "<p>* 임시 비밀번호 : <strong>" + tempPw + "</strong></p><br/>";
		}
		
		// 메일 전송
		mailService.sendEmail(email, subject, message);
		return 1;
	}
	
	/**
	 * 비밀번호 찾기(새로운 비밀번호 설정)
	 */
	@Transactional
	public int memberFindPassword(Map<String, Object> paramMap) throws Exception {
		String email = paramMap.get("email") + "";
		String tempPw = paramMap.get("temp_pw") + "";
		String newPw = paramMap.get("new_pw") + "";
		
		// 회원 조회
		Member member = memberRepository.findByEmail(email);
		// 임시 비밀번호 확인
		if(!passwordEncoder.matches(tempPw, member.getM_password())) { 
			return -1; // 임시 비밀번호 불일치
		}
		// 새로운 비밀번호 암호화
		String encryptionNewPw = passwordEncoder.encode(newPw);
		// 새로운 비밀번호로 변경
		member.updatePw(encryptionNewPw);
		memberRepository.save(member);
		return 1;
	}
	
}
