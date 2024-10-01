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
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int oi_no;
	
	@ManyToOne
    @JoinColumn(name = "o_no")
	@JsonBackReference
	private Order order; 
	//private int o_no; 
	
	private int oi_qty;
	
	@ManyToOne
    @JoinColumn(name = "p_no") 
	private Product product;
	 
	@Builder
	public OrderItem(int oi_no, int oi_qty, Order order, Product product) {
		this.oi_no = oi_no;
		this.oi_qty = oi_qty;
		this.order = order;
		this.product = product;
	}
	
	/*
	 * Order 세팅
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "OrderItem [oi_no=" + oi_no + ", oi_qty=" + oi_qty + ", order=" + order + ", product=" + product + "]";
	}

}
