package com.example.concertticketing.domain.concert.model;

import com.example.concertreservation.domain.common.entity.BaseEntity;
import com.example.concertreservation.domain.member.model.Member;
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
@Table(name = "SEAT", indexes = {
        @Index(name = "idx_concertDetailId", columnList = "concertDetailId")
})
@Entity
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONCERT_DETAIL_ID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertDetail concert;

    @Column(name = "SEAT_NO")
    private int seatNo;

    @Column(name = "PRICE")
    private Long price;

    // 5분간 임시 지정을 위함
    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING) // DB 저장 시 문자열로 저장
    @Column(name = "STATUS")
    private SeatStatus status; // AVAILABLE, TEMPORARY, RESERVED

    @Builder
    public Seat(Long id, Member member, ConcertDetail concert, int seatNo, Long price, LocalDateTime expiredAt, SeatStatus status) {
        this.id = id;
        this.member = member;
        this.concert = concert;
        this.seatNo = seatNo;
        this.price = price;
        this.expiredAt = expiredAt;
        this.status = status;
    }

    public boolean isAvailable() {
        if (this.status == SeatStatus.AVAILABLE) {
            return true;
        }

        return false;
    }

    public void updateStatus(SeatStatus status) {
        this.status = status;
    }
}
