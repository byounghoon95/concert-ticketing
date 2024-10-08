package com.example.concertservice.domain.model;

import com.example.concertservice.domain.common.entity.BaseEntity;
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

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONCERT_DETAIL_ID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertDetail concert;

    @Column(name = "SEAT_NO")
    private int seatNo;

    @Column(name = "PRICE")
    private Long price;

    @Column(name = "RESERVED_AT")
    private LocalDateTime reservedAt;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Builder
    public Seat(Long id, Long memberId, ConcertDetail concert, int seatNo, Long price, LocalDateTime reservedAt) {
        this.id = id;
        this.memberId = memberId;
        this.concert = concert;
        this.seatNo = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
    }

    public void updateReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public void updateMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
