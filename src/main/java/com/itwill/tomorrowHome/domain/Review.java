package com.itwill.tomorrowHome.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.itwill.tomorrowHome.dto.ReviewDto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int r_no;
	
	@Temporal(TemporalType.DATE)
	private Date r_date;
	
	private String r_content;
	private int r_score;
	private String r_title;
	private String m_id;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_no")
	@JsonBackReference
	private Product product;
	//private int p_no;
	
	@Builder
	public Review(int r_no, Date r_date, String r_content, int r_score, String r_title, String m_id, Product product) {
		this.r_no = r_no;
		this.r_date = r_date;
		this.r_content = r_content;
		this.r_score = r_score;
		this.r_title = r_title;
		this.m_id = m_id;
		this.product = product;
	}
	
	public static Review fromDto(ReviewDto dto) {
        return Review.builder()
                     .r_no(dto.getR_no())
                     .r_date(dto.getR_date())
                     .r_content(dto.getR_content())
                     .r_score(dto.getR_score())
                     .r_title(dto.getR_title())
                     .m_id(dto.getM_id())
                     .product(Product.builder().p_no(dto.getP_no()).build())
                     .build();
    }

	@Override
	public String toString() {
		return "Review [r_no=" + r_no + ", r_date=" + r_date + ", r_content=" + r_content + ", r_score=" + r_score
				+ ", r_title=" + r_title + ", m_id=" + m_id + ", product=" + product + "]";
	}

}
