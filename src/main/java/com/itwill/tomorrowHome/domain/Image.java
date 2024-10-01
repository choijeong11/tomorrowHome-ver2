package com.itwill.tomorrowHome.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int im_no;
	
	private String im_div;
	private String im_name;
	
	@ManyToOne
    @JoinColumn(name = "p_no")
	@JsonBackReference
	private Product product;
	
	@Builder
	public Image(int im_no, String im_div, String im_name, Product product) {
		super();
		this.im_no = im_no;
		this.im_div = im_div;
		this.im_name = im_name;
		this.product = product;
	}

	@Override
	public String toString() {
		return "Image [im_no=" + im_no + ", im_div=" + im_div + ", im_name=" + im_name + ", product=" + product + "]";
	}
	
}
