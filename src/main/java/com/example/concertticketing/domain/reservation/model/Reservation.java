package com.example.concertticketing.domain.reservation.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import com.example.concertticketing.domain.concert.model.Seat;
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
@Table(name = "RESERVATION")
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
    public Reservation(Seat seat, String concertName, Long price, int seatNo, Long memberId, LocalDateTime date, ReservationStatus status) {
        this.seat = seat;
        this.concertName = concertName;
        this.price = price;
        this.seatNo = seatNo;
        this.memberId = memberId;
        this.date = date;
        this.status = status;
    }
}
