package com.example.concertticketing.domain.common.interceptor;

import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.queue.service.QueueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenVerificationInterceptor implements HandlerInterceptor {

    private final QueueService queueService;

    @Autowired
    public TokenVerificationInterceptor(QueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long memberId = Long.valueOf(request.getHeader("memberId"));

        if (!queueService.verify(memberId)) {
            throw new CustomException(ErrorEnum.TOKEN_EXPIRED);
        }

        return true;
    }
}