package com.itwill.tomorrowHome.domain;

import java.util.Date;

import com.itwill.tomorrowHome.dto.QnaDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Qna {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int q_no;
	
	private String q_title;
	private String q_content;
	
	@Temporal(TemporalType.DATE)
	private Date q_date;
	
	private int q_count;
	//글의 논리적인 순서번호를 관리하기 위한 필드 (3개)
	private int q_group_no;    // 그룹번호 
	private int q_step;        // 그룹내 순서정보 
	private int q_depth;       // 답글 깊이 
	private String m_id;

	@Builder
	public Qna(int q_no, String q_title, String q_content, Date q_date, int q_count, int q_group_no, int q_step,
			int q_depth, String m_id) {
		this.q_no = q_no;
		this.q_title = q_title;
		this.q_content = q_content;
		this.q_date = q_date;
		this.q_count = q_count;
		this.q_group_no = q_group_no;
		this.q_step = q_step;
		this.q_depth = q_depth;
		this.m_id = m_id;
	}
	
    // DTO를 엔티티로 변환하는 메서드
    public static Qna fromDto(QnaDto dto) {
        return Qna.builder()
                .q_no(dto.getQ_no())
                .q_title(dto.getQ_title())
                .q_content(dto.getQ_content())
                .q_date(dto.getQ_date())
                .q_count(dto.getQ_count())
                .q_group_no(dto.getQ_group_no())
                .q_step(dto.getQ_step())
                .q_depth(dto.getQ_depth())
                .m_id(dto.getM_id())
                .build();
    }

	@Override
	public String toString() {
		return "Qna [q_no=" + q_no + ", " + (q_title != null ? "q_title=" + q_title + ", " : "")
				+ (q_content != null ? "q_content=" + q_content + ", " : "")
				+ (q_date != null ? "q_date=" + q_date + ", " : "") + "q_count=" + q_count + ", q_group_no="
				+ q_group_no + ", q_step=" + q_step + ", q_depth=" + q_depth + ", "
				+ (m_id != null ? "m_id=" + m_id : "") + "]";
	}
	
}
