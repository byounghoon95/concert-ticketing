package com.example.concertticketing.interfaces.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenVerificationInterceptor tokenVerificationInterceptor;

    @Autowired
    public WebConfig(TokenVerificationInterceptor tokenVerificationInterceptor) {
        this.tokenVerificationInterceptor = tokenVerificationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenVerificationInterceptor)
                .addPathPatterns("/api/concert/**", "/api/reserve/**", "/api/pay/**");
    }
}