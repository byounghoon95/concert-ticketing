package com.example.concertservice.interfaces.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenVerificationInterceptor implements HandlerInterceptor {

//    private final QueueService queueService;
//
//    @Autowired
//    public TokenVerificationInterceptor(QueueService queueService) {
//        this.queueService = queueService;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long memberId = Long.valueOf(request.getHeader("memberId"));
        log.info("Member Verification Request : {}", memberId);
//        if (!queueService.verify(memberId)) {
//            throw new CustomException(ErrorEnum.TOKEN_EXPIRED);
//        }

        return true;
    }
}