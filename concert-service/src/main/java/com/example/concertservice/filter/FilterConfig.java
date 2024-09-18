package com.example.concertservice.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean loggingFilterRegister() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(new LoggingFilter());
        return registrationBean;
    }
}
