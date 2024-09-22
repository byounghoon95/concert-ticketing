package com.example.reservationservice.interfaces.interceptor;

import com.example.reservationservice.domain.clients.QueueClient;
import com.example.reservationservice.exception.CustomException;
import com.example.reservationservice.exception.ErrorEnum;
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
    public TokenVerificationInterceptor(@Lazy QueueClient queueService) {
        this.queueClient = queueService;
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