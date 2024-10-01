package com.itwill.tomorrowHome.domain;

import java.util.ArrayList;
import java.util.List;

import com.itwill.tomorrowHome.dto.WishlistDto;

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
public class Wishlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int w_no;
	
	@OneToOne
	@JoinColumn(name = "p_no", referencedColumnName = "p_no")
	private Product product;
	
	private String m_id;
	
	@Builder
	public Wishlist(int w_no, String m_id, Product product) {
		this.w_no = w_no;
		this.m_id = m_id;
		this.product = product;
	}
	
    // DTO를 엔티티로 변환하는 메서드
    public static Wishlist fromDto(WishlistDto dto) {
    	List<Image> imageList = new ArrayList<Image>();
    	imageList.add(Image.builder()
		        		.im_name(dto.getIm_name())
		        		.build());
    	
        return Wishlist.builder()
                .w_no(dto.getW_no())  
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
		return "Wishlist [w_no=" + w_no + ", m_id=" + m_id + ", product=" + product + "]";
	}
	
}
