package com.example.concertservice.exception;

import com.example.concertservice.interfaces.api.common.response.CommonResponse;
import com.example.concertservice.util.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SlackClient slackClient;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e) {
        slackClient.sendMessage(e.getMessage());
        log.error("Unknown Error Occurred : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResponse.error(ErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse> handleCustomException(CustomException e) {
        log.error("Custom Error Occurred : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResponse.error(e.getCode(),e.getMessage()));
    }
}