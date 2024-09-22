package com.example.payservice.interfaces.interceptor;

import com.example.payservice.domain.clients.QueueClient;
import com.example.payservice.exception.CustomException;
import com.example.payservice.exception.ErrorEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenVerificationInterceptor implements HandlerInterceptor {

    private final QueueClient queueClient;

    @Autowired
    public TokenVerificationInterceptor(@Lazy QueueClient queueClient) {
        this.queueClient = queueClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long memberId = Long.valueOf(request.getHeader("memberId"));
        log.info("Member Verification Request : {}", memberId);
        if (!queueClient.verifyToken(memberId)) {
            throw new CustomException(ErrorEnum.TOKEN_EXPIRED);
        }

        return true;
    }
}