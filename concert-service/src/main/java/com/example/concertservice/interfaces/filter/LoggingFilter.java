package com.example.concertservice.interfaces.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest)request);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse)response);

        chain.doFilter(httpServletRequest,httpServletResponse);

        String requestURI = httpServletRequest.getRequestURI();
        String reqContent = new String(httpServletRequest.getContentAsByteArray());
        log.info("request url : {} , request body:{}" ,requestURI ,reqContent);

        int httpStatus = httpServletResponse.getStatus();
        String respContent = new String(httpServletResponse.getContentAsByteArray());
        log.info("response status:{} , responseBody:{}" ,httpStatus ,respContent);
        httpServletResponse.copyBodyToResponse();
    }
}