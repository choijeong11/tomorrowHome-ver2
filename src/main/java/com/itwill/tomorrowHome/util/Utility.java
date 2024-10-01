package com.itwill.tomorrowHome.util;

public class Utility {
	/**
	 * 문자-숫자 타입 변경 
	 */
    public static Integer parseInteger(String value) {
        try {
            return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
