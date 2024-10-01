package com.itwill.tomorrowHome.domain;

import java.util.ArrayList;
import java.util.List;

import com.itwill.tomorrowHome.dto.CartDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int c_no;
	
	private int c_qty;
	private String m_id;
	
	@OneToOne
	@JoinColumn(name = "p_no", referencedColumnName = "p_no")
	private Product product;
	
	@Builder
	public Cart(int c_no, int c_qty, Product product, String m_id) {
		this.c_no = c_no;
		this.c_qty = c_qty;
		this.product = product;
		this.m_id = m_id;
	}
	
    // DTO를 엔티티로 변환하는 메서드
    public static Cart fromDto(CartDto dto) {
    	List<Image> imageList = new ArrayList<Image>();
    	imageList.add(Image.builder()
		        		.im_name(dto.getIm_name())
		        		.build());
    	
        return Cart.builder()
                .c_no(dto.getC_no())  
                .c_qty(dto.getC_qty())
                .m_id(dto.getM_id())
                .product(Product.builder()
                		.p_no(dto.getP_no())
                		.p_name(dto.getP_name())
                		.p_price(dto.getP_price())
                		.imageList(imageList)
                		.build())
                .build();
    }
	
	@Override
	public String toString() {
		return "Cart [c_no=" + c_no + ", c_qty=" + c_qty + ", product=" + product + ", m_id=" + m_id + "]";
	}
	
}
