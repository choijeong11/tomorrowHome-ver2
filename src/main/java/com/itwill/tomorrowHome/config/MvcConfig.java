package com.itwill.tomorrowHome.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.itwill.tomorrowHome.controller")
public class MvcConfig implements WebMvcConfigurer {
	// CORS 설정
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://choibooboo.cafe24.com");
    }
	
    // 정적 자원 핸들러 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
        .addResourceLocations("classpath:/static/js/")
        .setCachePeriod(604800);

        registry.addResourceHandler("/css/**")
        .addResourceLocations("classpath:/static/css/")
        .setCachePeriod(604800);
        
        registry.addResourceHandler("/scss/**")
        .addResourceLocations("classpath:/static/scss/")
        .setCachePeriod(604800);
        
        registry.addResourceHandler("/fonts/**")
        .addResourceLocations("classpath:/static/fonts/")
        .setCachePeriod(604800);
        
        registry.addResourceHandler("/img/**")
        .addResourceLocations("classpath:/static/img/")
        .setCachePeriod(2592000);
        
        registry.addResourceHandler("/ckeditor/**")
        .addResourceLocations("classpath:/static/ckeditor/")
        .setCachePeriod(2592000);
    }
}
