package com.example.concertticketing.domain.reservation.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
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

    @ManyToOne
    @JoinColumn(name = "SEAT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Seat seat;

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
    public Reservation(Seat seat, String concertName, Long price, int seatNo, Long memberId, LocalDateTime date, ReservationStatus status, LocalDateTime createdAt) {
        this.seat = seat;
        this.concertName = concertName;
        this.price = price;
        this.seatNo = seatNo;
        this.memberId = memberId;
        this.date = date;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Reservation createReservation(Seat seat, Long memberId) {
        return Reservation.builder()
                .seat(seat)
                .concertName(seat.getConcert().getName())
                .price(seat.getPrice())
                .seatNo(seat.getSeatNo())
                .memberId(memberId)
                .date(seat.getConcert().getDate())
                .status(ReservationStatus.RESERVED)
                .build();
    }

    public void matchMember(Long domainMemberId, Long requestMemberId) {
        if (domainMemberId != requestMemberId) {
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
