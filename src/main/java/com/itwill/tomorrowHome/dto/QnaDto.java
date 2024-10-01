package com.itwill.tomorrowHome.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class QnaDto {
	private int q_no;
	private String q_title;
	private String q_content;
	private Date q_date;
	private int q_count;
	//글의 논리적인 순서번호를 관리하기 위한 필드 (3개)
	private int q_group_no;    // 그룹번호 
	private int q_step;        // 그룹내 순서정보 
	private int q_depth;       // 답글 깊이 
	private String m_id;
	private int rowNum;
	
	public QnaDto(int q_no, String q_title, String q_content, Date q_date, int q_count, int q_group_no, int q_step,
			int q_depth, String m_id, int rowNum) {
		this.q_no = q_no;
		this.q_title = q_title;
		this.q_content = q_content;
		this.q_date = q_date;
		this.q_count = q_count;
		this.q_group_no = q_group_no;
		this.q_step = q_step;
		this.q_depth = q_depth;
		this.m_id = m_id;
		this.rowNum = rowNum;
	}

	@Override
	public String toString() {
		return "QnaDto [q_no=" + q_no + ", q_title=" + q_title + ", q_content=" + q_content + ", q_date=" + q_date
				+ ", q_count=" + q_count + ", q_group_no=" + q_group_no + ", q_step=" + q_step + ", q_depth=" + q_depth
				+ ", m_id=" + m_id + ", rowNum=" + rowNum + "]";
	}
	
}
