package com.example.concertticketing.domain.pay.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import com.example.concertticketing.domain.reservation.model.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PAY")
@Entity
public class Pay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    @Column(name = "AMOUNT")
    private Long amount;

    @Enumerated(EnumType.STRING) // DB 저장 시 문자열로 저장
    @Column(name = "STATUS")
    private PayStatus status;

    @Builder
    public Pay(Reservation reservation, Long amount, PayStatus status) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = status;
    }

    public static Pay createPay(Reservation reservation) {
        return Pay.builder()
                .reservation(reservation)
                .amount(reservation.getPrice())
                .status(PayStatus.PAYED)
                .build();
    }
}
