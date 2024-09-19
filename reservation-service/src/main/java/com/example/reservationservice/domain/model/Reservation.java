package com.example.reservationservice.domain.model;

import com.example.reservationservice.domain.common.entity.BaseEntity;
import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.exception.CustomException;
import com.example.reservationservice.exception.ErrorEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RESERVATION", indexes = {
        @Index(name = "idx_seat_id", columnList = "seat_id")
})
@Entity
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SEAT_ID")
    private Long seatId;

    @Column(name = "CONCERT_NAME")
    private String concertName;

    @Column(name = "SEAT_NO")
    int seatNo;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "CONCERT_PRICE")
    private Long price;

    @Column(name = "CONCERT_DATE")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING) // DB 저장 시 문자열로 저장
    @Column(name = "STATUS")
    private ReservationStatus status;

    @Builder
    public Reservation(Long seatId, String concertName, Long price, int seatNo, Long memberId, LocalDateTime date, ReservationStatus status, LocalDateTime createdAt) {
        this.seatId = seatId;
        this.concertName = concertName;
        this.price = price;
        this.seatNo = seatNo;
        this.memberId = memberId;
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Reservation createReservation(SeatResponse seat, Long memberId) {
        return Reservation.builder()
                .seatId(seat.getId())
//                .concertName(seat.getConcert().getName())
                .price(seat.getPrice())
                .seatNo(seat.getSeatNo())
                .memberId(memberId)
//                .date(seat.getConcert().getDate())
                .status(ReservationStatus.RESERVED)
                .build();
    }

    public void matchMember(Long domainMemberId, Long requestMemberId) {
        if (!domainMemberId.equals(requestMemberId)) {
            throw new CustomException(ErrorEnum.MEMBER_NOT_MATCH);
        }
    }

    public void isAvailable() {
        if (this.createdAt.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new CustomException(ErrorEnum.NO_RESERVATION);
        }
    }

    public static void checkTempReserved(LocalDateTime reservedAt) {
        LocalDateTime now = LocalDateTime.now();
        if (reservedAt != null && reservedAt.plusMinutes(5).isAfter(now)) {
            throw new CustomException(ErrorEnum.RESERVED_SEAT);
        }
    }
}
