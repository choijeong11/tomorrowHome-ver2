package com.itwill.tomorrowHome.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CartDto {
    private int c_no;
    private int c_qty;
    private String m_id;
    private int p_no;
    private String p_name;
    private int p_price;
    private String im_name;

    @QueryProjection
    public CartDto(int c_no, int c_qty, String m_id, int p_no, String p_name, int p_price, String im_name) {
        this.c_no = c_no;
        this.c_qty = c_qty;
        this.m_id = m_id;
        this.p_no = p_no;
        this.p_name = p_name;
        this.p_price = p_price;
        this.im_name = im_name;
    }
    
}
