package com.itwill.tomorrowHome.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.itwill.tomorrowHome", excludeFilters = {
        @ComponentScan.Filter(org.springframework.stereotype.Controller.class),
        @ComponentScan.Filter(org.springframework.web.bind.annotation.RestController.class)
})
public class AppConfig {

}
