package com.example.concertticketing.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 토큰 : 1xxx
 * 콘서트 : 2xxx
 * 멤버 : 3xxx
 * 결제 : 4xxx
 * 예약 : 5xxx
 * */
@Getter
@RequiredArgsConstructor
public enum ErrorEnum {

    SUCCESS("0000","SUCCESS"),
    TOKEN_EXPIRED("1000","TOKEN IS EXPIRED"),

    MEMBER_NOT_FOUND("3000", "MEMBER DOES NOT EXIST"),
    RESERVED_SEAT("5000", "SEAT IS ALREADY RESERVED"),
    UNKNOWN_ERROR("9999","UNKNOWN_ERROR"),
    ;

    private final String code;
    private final String message;
}
