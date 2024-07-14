package com.example.concertticketing.domain.concert.model;

/**
 * AVAILABLE : 예약 가능 상태
 * TEMPORARY : 5분간 임시 배정 상태, 5분 후 AVAILABLE 로 변경
 * RESERVED : 예약 확정, 취소 시 AVAILABLE 로 변경
 * */
public enum SeatStatus {
    AVAILABLE, TEMPORARY, RESERVED
}
