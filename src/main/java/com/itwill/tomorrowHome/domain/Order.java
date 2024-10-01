package com.itwill.tomorrowHome.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int o_no;
	
	private String o_desc;
	
	@Temporal(TemporalType.DATE)
	private Date o_date;
	private int o_price;
	private String o_pay_method;
	private String o_status;
	private String o_rv_name;
	private String o_rv_phone;
	private String o_rv_address;
	private String o_rv_post;
	private String o_message;
	private String m_id;
	
	@OneToMany(mappedBy = "order")
	@JsonManagedReference
	private List<OrderItem> orderItemList = new ArrayList<OrderItem>();

	@Builder
	public Order(int o_no, String o_desc, Date o_date, int o_price, String o_pay_method, String o_status,
			String o_rv_name, String o_rv_phone, String o_rv_address, String o_rv_post, String o_message, String m_id,
			List<OrderItem> orderItemList) {
		this.o_no = o_no;
		this.o_desc = o_desc;
		this.o_date = o_date;
		this.o_price = o_price;
		this.o_pay_method = o_pay_method;
		this.o_status = o_status;
		this.o_rv_name = o_rv_name;
		this.o_rv_phone = o_rv_phone;
		this.o_rv_address = o_rv_address;
		this.o_rv_post = o_rv_post;
		this.o_message = o_message;
		this.m_id = m_id;
		this.orderItemList = orderItemList;
	}

	@Override
	public String toString() {
		return "Order [o_no=" + o_no + ", o_desc=" + o_desc + ", o_date=" + o_date + ", o_price=" + o_price
				+ ", o_pay_method=" + o_pay_method + ", o_status=" + o_status + ", o_rv_name=" + o_rv_name
				+ ", o_rv_phone=" + o_rv_phone + ", o_rv_address=" + o_rv_address + ", o_rv_post=" + o_rv_post
				+ ", o_message=" + o_message + ", m_id=" + m_id + ", orderItemList=" + orderItemList + "]";
	}
	
}
