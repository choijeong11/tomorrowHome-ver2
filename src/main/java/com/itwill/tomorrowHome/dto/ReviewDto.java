package com.itwill.tomorrowHome.dto;

import java.util.Date;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.domain.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReviewDto {
	private int r_no;
	private Date r_date;
	private String r_content;
	private int r_score;
	private String r_title;
	private String m_id;
	private Product product;
	private int p_no;
	
	public ReviewDto(int r_no, Date r_date, String r_content, int r_score, String r_title, String m_id, Product product,
			int p_no) {
		this.r_no = r_no;
		this.r_date = r_date;
		this.r_content = r_content;
		this.r_score = r_score;
		this.r_title = r_title;
		this.m_id = m_id;
		this.product = product;
		this.p_no = p_no;
	}
	
	public ReviewDto(Review review) {
		this.r_no = review.getR_no();
		this.r_date = review.getR_date();
		this.r_content = review.getR_content();
		this.r_score = review.getR_score();
		this.r_title = review.getR_title();
		this.m_id = review.getM_id();
		this.product = review.getProduct();
		this.p_no = review.getProduct().getP_no();
	}

	@Override
	public String toString() {
		return "ReviewDto [r_no=" + r_no + ", r_date=" + r_date + ", r_content=" + r_content + ", r_score=" + r_score
				+ ", r_title=" + r_title + ", m_id=" + m_id + ", product=" + product + ", p_no=" + p_no + "]";
	}
}
