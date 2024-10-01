package com.itwill.tomorrowHome.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WishlistDto {
    private int w_no;
    private String m_id;
    private int p_no;
    private String p_name;
    private int p_price;
    private String im_name;

    @QueryProjection
    public WishlistDto(int w_no, String m_id, int p_no, String p_name, int p_price, String im_name) {
        this.w_no = w_no;
        this.m_id = m_id;
        this.p_no = p_no;
        this.p_name = p_name;
        this.p_price = p_price;
        this.im_name = im_name;
    }
    
}
