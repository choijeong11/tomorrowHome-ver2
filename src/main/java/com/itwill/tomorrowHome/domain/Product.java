package com.itwill.tomorrowHome.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int p_no;
	
	private String p_name;
	private int p_price;
	private String p_desc;
	private Date p_date;
	private Double p_avg_score;
	private String p_color;
	private String p_brand;
	private int p_count;
	private String p_concept;
	private int cg_no;
	
	@OneToMany(mappedBy = "product")
	@JsonManagedReference
	private List<Image> imageList;
	
	@OneToMany(mappedBy = "product")
	@JsonManagedReference
	private List<Review> reviewList;
	
	@Builder
	public Product(int p_no, String p_name, int p_price, String p_desc, Date p_date, Double p_avg_score, String p_color,
			String p_brand, int p_count, String p_concept, int cg_no, List<Image> imageList, List<Review> reviewList) {
		this.p_no = p_no;
		this.p_name = p_name;
		this.p_price = p_price;
		this.p_desc = p_desc;
		this.p_date = p_date;
		this.p_avg_score = p_avg_score;
		this.p_color = p_color;
		this.p_brand = p_brand;
		this.p_count = p_count;
		this.p_concept = p_concept;
		this.cg_no = cg_no;
		this.imageList = imageList;
		this.reviewList = reviewList;
	}
	
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Product [p_no=" + p_no + ", p_name=" + p_name + ", p_price=" + p_price + ", p_desc=" + p_desc
				+ ", p_date=" + p_date + ", p_avg_score=" + p_avg_score + ", p_color=" + p_color + ", p_brand="
				+ p_brand + ", p_count=" + p_count + ", p_concept=" + p_concept + ", cg_no=" + cg_no + ", imageList="
				+ (imageList != null ? imageList.subList(0, Math.min(imageList.size(), maxLen)) : null)
				+ ", reviewList="
				+ (reviewList != null ? reviewList.subList(0, Math.min(reviewList.size(), maxLen)) : null) + "]";
	}
	
}
