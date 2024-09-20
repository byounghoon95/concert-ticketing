package com.example.payservice.domain.model;

import com.example.payservice.domain.common.entity.BaseEntity;
import com.example.payservice.external.ReservePayResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PAY", indexes = {
        @Index(name = "idx_reservation_id", columnList = "reservation_id")
})
@Entity
public class Pay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @Column(name = "AMOUNT")
    private Long amount;

    @Enumerated(EnumType.STRING) // DB 저장 시 문자열로 저장
    @Column(name = "STATUS")
    private PayStatus status;

    @Builder
    public Pay(Long reservationId, Long amount, PayStatus status) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
    }

    public static Pay createPay(ReservePayResponse reservation) {
        return Pay.builder()
                .reservationId(reservation.reservationId())
                .amount(reservation.price())
                .status(PayStatus.PAYED)
                .build();
    }
}
