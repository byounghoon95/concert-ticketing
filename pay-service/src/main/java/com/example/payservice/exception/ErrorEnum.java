package com.example.payservice.exception;

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
    NO_MORE_ACTIVE_TOKEN("1001","AVAILABLE ACTIVE TOKEN IS FULL"),
    NO_WAIT_TOKEN("1002","WAIT TOKEN DOES NOT EXIST"),
    NO_CONCERT("2000","CONCERT DOES NOT EXIST"),
    NO_SEAT("2001","SEAT DOES NOT EXIST"),
    MEMBER_NOT_FOUND("3000", "MEMBER DOES NOT EXIST"),
    MEMBER_NOT_MATCH("4000", "MEMBER INFO IS NOT EQUAL"),
    NOT_ENOUGH_BALANCE("4001", "BALANCE IS LESS THAN REQUIRED AMOUNT"),
    NO_PAY("4002", "NO PAY"),
    RESERVED_SEAT("5000", "SEAT IS ALREADY RESERVED"),
    NO_RESERVATION("5001", "NO AVAILABLE RESERVATION"),
    CONVERT_ERROR("9000","JSON CONVERT FAIL"),
    SLACK_ERROR("9001","SLACK_ERROR"),
    NO_OUTBOX("9002", "NO OUTBOX DATA"),
    UNKNOWN_ERROR("9999","UNKNOWN_ERROR"),
    ;

    private final String code;
    private final String message;
}
