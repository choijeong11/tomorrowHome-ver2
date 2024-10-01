package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Member;
import com.itwill.tomorrowHome.dto.MemberDto;
import com.itwill.tomorrowHome.service.AuthService;
import com.itwill.tomorrowHome.service.MemberService;

import jakarta.servlet.http.HttpSession;

@RestController
public class MemberRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberService memberService;
	@Autowired
	private AuthService authService;

	/**
	 * 회원가입
	 */
	@RequestMapping("member_register_rest")
	public Map<String, Object> member_register_rest(@RequestBody MemberDto memberDto, Model model) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			int result = memberService.createMember(memberDto);
			if (result == -1) {
				model.addAttribute("fMember", memberDto);
				resultMap.put("errorCode", -1);
				resultMap.put("errorMsg", messageSource.getMessage("member.duplicate", null, Locale.KOREA));
			} else if (result == -2) {
				resultMap.put("errorCode", -2);
				resultMap.put("errorMsg", messageSource.getMessage("member.email.duplicate", new String[]{memberDto.getM_id()}, Locale.KOREA));
			} else if (result == -3) {
				resultMap.put("errorCode", -3);
				resultMap.put("errorMsg", messageSource.getMessage("member.verification.error", new String[]{memberDto.getM_id()}, Locale.KOREA));
			} else if (result == 1) {
				resultMap.put("errorCode", 1);
				resultMap.put("errorMsg", messageSource.getMessage("member.insert", new String[]{memberDto.getM_id()}, Locale.KOREA));
			}
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 회원정보수정
	 */
	@RequestMapping("member_account_update_rest")
	public Map<String, Object> member_account_update_rest(@RequestBody MemberDto memberDto, HttpSession session) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Member sMember = (Member)session.getAttribute("sMember");
			if(!passwordEncoder.matches(memberDto.getM_password(), sMember.getM_password())) { 
				resultMap.put("errorCode", -1);
				resultMap.put("errorMsg", messageSource.getMessage("member.pw.error", null, Locale.KOREA));
				return resultMap;
			}
			
			Member updateMember = memberService.updateMember(memberDto);
			session.setAttribute("sMember", updateMember);
			resultMap.put("errorCode", 1);
			resultMap.put("errorMsg", messageSource.getMessage("common.update", null, Locale.KOREA));
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 회원 탈퇴
	 */
	@RequestMapping("member_withdrawal_rest")
	public Map<String, Object> member_withdrawal_rest(@RequestBody MemberDto memberDto, HttpSession session) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			memberService.deleteMember(memberDto);
			session.invalidate();
			resultMap.put("errorCode", 1);
			resultMap.put("errorMsg", messageSource.getMessage("member.delete", new String[]{memberDto.getM_id()}, Locale.KOREA));
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 회원 정보
	 */
	@RequestMapping("member_info_rest")
	public Map<String, Object> member_info_rest(HttpSession session) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap.put("errorCode", 1);
			resultMap.put("data", session.getAttribute("sMember"));
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 회원 인증 이메일 전송
	 */
	@RequestMapping("member_send_verification_email")
    public Map<String, Object> member_send_email(@RequestBody String m_email) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 이메일 중복 확인
			Member member = memberService.findMemberByEmail(m_email);
			if(member != null) {
				resultMap.put("errorCode", -1);
				resultMap.put("errorMsg", messageSource.getMessage("member.email.duplicate", null, Locale.KOREA));
				return resultMap;
			}
			
			// 이메일 발송
	        memberService.sendMemberVerificationEmail(m_email); 
	        resultMap.put("errorCode", 1);
			resultMap.put("errorMsg", messageSource.getMessage("email.send.success", null, Locale.KOREA));
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
    }
	
	/**
	 * 회원 이메일 인증
	 */
	@RequestMapping("member_email_verification")
	public Map<String, Object> member_email_verification(@RequestBody Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			String email = paramMap.get("email") + "";
			String inputCode = paramMap.get("code") + "";
			
			int verifiedResult = authService.verifyCode(inputCode, email);
			String resultMsg = "";
	        if (verifiedResult == 1) { // 인증 완료
	        	resultMap.put("errorCode", 1);
	        	resultMsg = "email.verification.success";
	        } else if(verifiedResult == -1) { // 기한 만료 
	        	resultMap.put("errorCode", -1);
	        	resultMsg = "email.verification.expired";
	        } else if(verifiedResult == -2) { // 코드 불일치
	        	resultMap.put("errorCode", -2);
	        	resultMsg = "email.verification.discord";
	        } else {
	        	resultMap.put("errorCode", -3);
	        	resultMsg = "email.verification.error";
	        }
	        resultMap.put("errorMsg", messageSource.getMessage(resultMsg, null, Locale.KOREA));
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 아이디/비밀번호 찾기 메일 전송
	 */
	@RequestMapping("member_find_send_email")
	public Map<String, Object> member_find_send_email(@RequestBody Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, Object> dataMap = null;
		try {
			String type = paramMap.get("type") + "";
			String email = paramMap.get("email") + "";
			
	        int result = memberService.sendMemberFindEmail(type, email); // 이메일 발송
	        if(result > 0) {
	        	resultMap.put("errorCode", 1);
				resultMap.put("errorMsg", messageSource.getMessage("email.send.success", null, Locale.KOREA));
				dataMap = new HashMap<>();
				dataMap.put("type", type);
				resultMap.put("data", dataMap);
	        }else if(result == -1) { // 회원 없음
	        	resultMap.put("errorCode", -1);
				resultMap.put("errorMsg", messageSource.getMessage("member.find.exist.error", null, Locale.KOREA));
	        }
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 비밀번호 찾기(새로운 비밀번호 설정)
	 */
	@RequestMapping("member_find_password")
	public Map<String, Object> member_find_password(@RequestBody Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			int result = memberService.memberFindPassword(paramMap); 
			if(result > 0) {
				resultMap.put("errorCode", 1);
				resultMap.put("errorMsg", messageSource.getMessage("member.find.pw.success", null, Locale.KOREA));
			}else if(result == -1) { // 임시 비밀번호 불일치
				resultMap.put("errorCode", -1);
				resultMap.put("errorMsg", messageSource.getMessage("member.find.pw.error", null, Locale.KOREA));
			}
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", -999);
			resultMap.put("errorMsg", messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		return resultMap;
	}

}
